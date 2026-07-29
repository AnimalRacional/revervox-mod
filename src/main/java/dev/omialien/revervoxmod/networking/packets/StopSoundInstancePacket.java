package dev.omialien.revervoxmod.networking.packets;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopSoundInstancePacket {
    private final int entityId;
    public StopSoundInstancePacket(int entity) {
        this.entityId = entity;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public static StopSoundInstancePacket decode(FriendlyByteBuf buf) {
        return new StopSoundInstancePacket(buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        RevervoxMod.LOGGER.debug("RECEIVED STOP PACKET");
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }
            Entity entity = level.getEntity(entityId);
            if (entity instanceof RevervoxGeoEntity revervox) {
                revervox.forceStopPlaying();
            }
        }));
    }
}
