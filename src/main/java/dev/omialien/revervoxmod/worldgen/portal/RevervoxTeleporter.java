package dev.omialien.revervoxmod.worldgen.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class RevervoxTeleporter implements ITeleporter {
    private final BlockPos target;
    private final float yRot, xRot;

    public RevervoxTeleporter(BlockPos target, float yRot, float xRot) {
        this.target = target;
        this.yRot = yRot;
        this.xRot = xRot;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel from, ServerLevel to,
                              float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity e = repositionEntity.apply(false);
        e.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
        e.setYRot(yRot);
        e.setXRot(xRot);
        e.setDeltaMovement(Vec3.ZERO);
        e.resetFallDistance();
        return e;
    }

    @Override
    public boolean playTeleportSound(ServerPlayer p, ServerLevel from, ServerLevel to) {
        return false;
    }
}

