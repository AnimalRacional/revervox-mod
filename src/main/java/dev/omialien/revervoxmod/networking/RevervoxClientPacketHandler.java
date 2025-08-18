package dev.omialien.revervoxmod.networking;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.sound.EntityFollowingSoundInstance;
import dev.omialien.revervoxmod.networking.packets.SoundInstancePacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Mod(value = RevervoxMod.MOD_ID, dist = Dist.CLIENT)
public class RevervoxClientPacketHandler {
    public static void handleSoundInstancePacket(final SoundInstancePacket packet, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            RevervoxMod.LOGGER.debug("Sound instance packet received!");
            Level level = net.minecraft.client.Minecraft.getInstance().level;
            if (level != null && level.getEntity(packet.entityId()) instanceof LivingEntity entity){
                net.minecraft.client.Minecraft.getInstance().getSoundManager().play(new EntityFollowingSoundInstance(entity, packet.sound(), packet.source(), packet.looping()));
            } else {
                RevervoxMod.LOGGER.error("SoundInstancePacket Could not find entity with id {}", packet.entityId());
            }
        });
    }
}
