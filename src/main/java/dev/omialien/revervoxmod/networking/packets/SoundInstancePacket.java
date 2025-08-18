package dev.omialien.revervoxmod.networking.packets;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record SoundInstancePacket(int entityId, SoundEvent sound, SoundSource source, boolean looping) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SoundInstancePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "sound_instance_packet")
    );

    public static final StreamCodec<FriendlyByteBuf, SoundInstancePacket> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, SoundInstancePacket>() {
        @Override
        public SoundInstancePacket decode(FriendlyByteBuf buffer) {
            return new SoundInstancePacket(buffer.readInt(), SoundEvent.createVariableRangeEvent(buffer.readResourceLocation()), SoundSource.values()[buffer.readInt()], buffer.readBoolean());
        }

        @Override
        public void encode(FriendlyByteBuf o, SoundInstancePacket soundInstancePacket) {
            o.writeInt(soundInstancePacket.entityId());
            o.writeResourceLocation(soundInstancePacket.sound().getLocation());
            o.writeInt(soundInstancePacket.source().ordinal());
            o.writeBoolean(soundInstancePacket.looping());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
