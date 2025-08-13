package dev.omialien.revervoxmod.networking.packets;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.sound.EntityFollowingSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddSoundInstancePacket {
    private final int entityID;
    private final SoundEvent sound;
    private final SoundSource soundSource;
    private final boolean looping;
    public AddSoundInstancePacket(int entityID, SoundEvent sound, SoundSource soundSource, boolean looping) {
        RevervoxMod.LOGGER.debug("Created AddSoundInstancePacket");
        this.entityID = entityID;
        this.sound = sound;
        this.soundSource = soundSource;
        this.looping = looping;
    }

    public void encode(FriendlyByteBuf buffer){
        // Fill the buffer with packet
        RevervoxMod.LOGGER.debug("Encoded AddSoundInstancePacket");
        buffer.writeInt(entityID);
        buffer.writeResourceLocation(sound.getLocation());
        buffer.writeInt(soundSource.ordinal());
        buffer.writeBoolean(looping);
    }

    public static AddSoundInstancePacket decode(FriendlyByteBuf buffer){
        // Create the packet from the buffer
        RevervoxMod.LOGGER.debug("Decoded AddSoundInstancePacket");
        return new AddSoundInstancePacket(buffer.readInt(), SoundEvent.createVariableRangeEvent(buffer.readResourceLocation()) , SoundSource.values()[buffer.readInt()], buffer.readBoolean());
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<NetworkEvent.Context> ctx){
        // Handle the packet
        RevervoxMod.LOGGER.debug("Handling AddSoundInstancePacket");
        ctx.get().enqueueWork(() -> {
            RevervoxMod.LOGGER.debug("Sound instance packet received!");
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->{
                Level level = net.minecraft.client.Minecraft.getInstance().level;
                if (level != null && level.getEntity(entityID) instanceof LivingEntity entity){
                    net.minecraft.client.Minecraft.getInstance().getSoundManager().play(new EntityFollowingSoundInstance(entity, sound, soundSource, looping));
                } else {
                    RevervoxMod.LOGGER.error("Could not find entity with id {}", entityID);
                }

            });
        });
        ctx.get().setPacketHandled(true);
    }
}
