package dev.omialien.revervoxmod.worldgen.dimension;

import dev.omialien.revervoxmod.worldgen.portal.RevervoxTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NightmareInstances extends SavedData {
    private static final String NAME = "revervox_instances";

    private final Map<UUID, Integer> slots = new HashMap<>();
    private final Map<UUID, ReturnPoint> returns = new HashMap<>();

    public record ReturnPoint(ResourceKey<Level> dim, BlockPos pos, float yRot, float xRot) {}

    public static NightmareInstances get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(NightmareInstances::load, NightmareInstances::new, NAME);
    }

    public boolean isNightmare(ResourceKey<Level> key) {
        return RevervoxDimensions.HOUSE_LEVELS.contains(key)
                || key.equals(RevervoxDimensions.PIT_LEVEL_KEY);
    }

    public int acquire(UUID id) {
        Integer existing = slots.get(id);
        if (existing != null) return existing;

        for (int i = 0; i < RevervoxDimensions.HOUSE_LEVELS.size(); i++) {
            if (!slots.containsValue(i)) {
                slots.put(id, i);
                setDirty();
                return i;
            }
        }
        return -1;
    }

    public void release(MinecraftServer server, UUID id) {
        Integer slot = slots.remove(id);
        returns.remove(id);
        setDirty();
        if (slot == null) return;
        ServerLevel level = server.getLevel(RevervoxDimensions.HOUSE_LEVELS.get(slot));
        if (level != null) wipe(level);
    }

    public void setReturn(ServerPlayer p) {
        returns.put(p.getUUID(), new ReturnPoint(
                p.level().dimension(), p.blockPosition(), p.getYRot(), p.getXRot()));
        setDirty();
    }

    public void sendHome(ServerPlayer sp) {
        ReturnPoint r = returns.get(sp.getUUID());
        ServerLevel dest = r != null ? sp.server.getLevel(r.dim()) : null;
        if (dest == null) dest = sp.server.overworld();

        BlockPos pos = r != null ? r.pos() : dest.getSharedSpawnPos();
        float y = r != null ? r.yRot() : 0f;
        float x = r != null ? r.xRot() : 0f;

        release(sp.server, sp.getUUID());
        sp.changeDimension(dest, new RevervoxTeleporter(pos, y, x));
    }


    public static boolean buildHouse(ServerPlayer sp, ServerLevel instance) {
        BlockPos bed = sp.getRespawnPosition();
        ServerLevel src = sp.server.getLevel(sp.getRespawnDimension());
        if (bed == null || src == null) return false;

        BlockPos origin = bed.offset(-RevervoxDimensions.R_XZ,
                -RevervoxDimensions.DOWN_Y,
                -RevervoxDimensions.R_XZ);

        force(src, origin, true);
        StructureTemplate template = new StructureTemplate();
        template.fillFromWorld(src, origin, RevervoxDimensions.SIZE, true, null);
        force(src, origin, false);

        wipe(instance);
        force(instance, RevervoxDimensions.PASTE, true);

        template.placeInWorld(instance, RevervoxDimensions.PASTE, RevervoxDimensions.PASTE,
                new StructurePlaceSettings(), instance.getRandom(), Block.UPDATE_CLIENTS);

        placeFloor(instance);
        return true;
    }

    public static void wipe(ServerLevel level) {
        BlockPos min = RevervoxDimensions.PASTE.below();
        BlockPos max = RevervoxDimensions.PASTE.offset(RevervoxDimensions.SIZE).offset(-1, -1, -1);
        BlockState air = Blocks.AIR.defaultBlockState();

        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            if (!level.getBlockState(p).isAir()) level.setBlock(p, air, 2);
        }

        AABB box = new AABB(min, max.offset(1, 1, 1));
        for (Entity e : level.getEntities(EntityTypeTest.forClass(Entity.class), box,
                e -> !(e instanceof Player))) {
            e.discard();
        }
        force(level, RevervoxDimensions.PASTE, false);
    }

    private static void placeFloor(ServerLevel level) {
        BlockState barrier = Blocks.BARRIER.defaultBlockState();
        int y = RevervoxDimensions.PASTE.getY() - 1;
        for (int x = 0; x < RevervoxDimensions.SIZE.getX(); x++) {
            for (int z = 0; z < RevervoxDimensions.SIZE.getZ(); z++) {
                level.setBlock(new BlockPos(RevervoxDimensions.PASTE.getX() + x, y,
                        RevervoxDimensions.PASTE.getZ() + z), barrier, 2);
            }
        }
    }

    private static void force(ServerLevel level, BlockPos origin, boolean add) {
        int x0 = origin.getX() >> 4, z0 = origin.getZ() >> 4;
        int x1 = (origin.getX() + RevervoxDimensions.SIZE.getX()) >> 4;
        int z1 = (origin.getZ() + RevervoxDimensions.SIZE.getZ()) >> 4;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++)
                level.setChunkForced(x, z, add);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        slots.forEach((id, slot) -> {
            CompoundTag e = new CompoundTag();
            e.putUUID("id", id);
            e.putInt("slot", slot);
            ReturnPoint r = returns.get(id);
            if (r != null) {
                e.putString("rdim", r.dim().location().toString());
                e.putLong("rpos", r.pos().asLong());
                e.putFloat("ry", r.yRot());
                e.putFloat("rx", r.xRot());
            }
            list.add(e);
        });
        tag.put("slots", list);
        return tag;
    }

    private static NightmareInstances load(CompoundTag tag) {
        NightmareInstances m = new NightmareInstances();
        for (Tag t : tag.getList("slots", Tag.TAG_COMPOUND)) {
            CompoundTag e = (CompoundTag) t;
            UUID id = e.getUUID("id");
            m.slots.put(id, e.getInt("slot"));
            if (e.contains("rdim")) {
                m.returns.put(id, new ReturnPoint(
                        ResourceKey.create(Registries.DIMENSION, new ResourceLocation(e.getString("rdim"))),
                        BlockPos.of(e.getLong("rpos")), e.getFloat("ry"), e.getFloat("rx")));
            }
        }
        return m;
    }
}