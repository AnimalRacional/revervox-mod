package dev.omialien.revervoxmod.networking.packets;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechat_recording.voicechat.RevervoxVoicechatPlugin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PrivacyModePacket {
    private final boolean state;

    public PrivacyModePacket(boolean s){
        RevervoxMod.LOGGER.debug("Created PrivacyModePacket");
        this.state = s;
    }

    public void encode(FriendlyByteBuf buffer){
        // Fill the buffer with packet
        RevervoxMod.LOGGER.debug("Encoded PrivacyModePacket");
        buffer.writeBoolean(this.state);
    }

    public static PrivacyModePacket decode(FriendlyByteBuf buffer){
        // Create the packet from the buffer
        RevervoxMod.LOGGER.debug("Decoded PrivacyModePacket");
        return new PrivacyModePacket(buffer.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        // Handle the packet
        RevervoxMod.LOGGER.debug("Handling PrivacyModePacket");
        ctx.get().enqueueWork(() -> {
            RevervoxMod.LOGGER.debug("PrivacyModePacket received!");
            ServerPlayer sender = ctx.get().getSender();
            if(sender != null){
                RevervoxMod.LOGGER.debug("Setting privacy mode for {} to {}", sender.getName(), state);
                RevervoxVoicechatPlugin.setPrivacy(sender.getUUID(), state);
            } else {
                RevervoxMod.LOGGER.warn("Received PrivacyModePacket without sender?");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
