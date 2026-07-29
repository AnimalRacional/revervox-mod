package dev.omialien.revervoxmod.networking.packets;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StopSoundInstancePacket(int entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StopSoundInstancePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "stop_sound_instance_packet")
    );

    public static final StreamCodec<FriendlyByteBuf, StopSoundInstancePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StopSoundInstancePacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new StopSoundInstancePacket(friendlyByteBuf.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf o, StopSoundInstancePacket soundInstancePacket) {
            o.writeInt(soundInstancePacket.entityId());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
