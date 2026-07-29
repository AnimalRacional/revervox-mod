package dev.omialien.revervoxmod.networking;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.entity.custom.sound.EntityFollowingSoundInstance;
import dev.omialien.revervoxmod.entity.custom.sound.RevervoxFollowingSoundInstance;
import dev.omialien.revervoxmod.networking.packets.SoundInstancePacket;
import dev.omialien.revervoxmod.networking.packets.StopSoundInstancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RevervoxClientPacketHandler {
    public static void handleSoundInstancePacket(final SoundInstancePacket packet, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            RevervoxMod.LOGGER.debug("Sound instance packet received!");
            Level level = net.minecraft.client.Minecraft.getInstance().level;
            if (level == null) {
                RevervoxMod.LOGGER.error("Got sound instance packet without level");
                return;
            }
            Entity entity = level.getEntity(packet.entityId());
            if (entity instanceof RevervoxGeoEntity lEntity) {
                net.minecraft.client.Minecraft.getInstance().getSoundManager().play(new RevervoxFollowingSoundInstance(lEntity, packet.sound(), packet.source()));
            }
            else if (level.getEntity(packet.entityId()) instanceof LivingEntity lEntity){
                net.minecraft.client.Minecraft.getInstance().getSoundManager().play(new EntityFollowingSoundInstance(lEntity, packet.sound(), packet.source(), packet.looping()));
            } else {
                RevervoxMod.LOGGER.error("SoundInstancePacket Could not find entity with id {}", packet.entityId());
            }
        });
    }

    public static void handleStopSoundInstancePacket(final StopSoundInstancePacket packet, final IPayloadContext ctx) {
        RevervoxMod.LOGGER.debug("Got stop sound instance packet");
        ctx.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) {
                RevervoxMod.LOGGER.error("Got stop sound instance packet without level");
                return;
            }
            Entity entity = level.getEntity(packet.entityId());
            if (entity instanceof RevervoxGeoEntity lEntity) {
                lEntity.forceStopPlaying();
            } else {
                RevervoxMod.LOGGER.error("StopSoundInstancePacket could not find entity with id {}", packet.entityId());
            }
        });
    }
}
