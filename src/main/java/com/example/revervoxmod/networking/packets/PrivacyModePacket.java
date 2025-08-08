package com.example.revervoxmod.networking.packets;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PrivacyModePacket {
    private boolean state;

    public PrivacyModePacket(boolean s){
        RevervoxMod.LOGGER.info("CREATED PACKET!");
        this.state = s;
    }

    public void encode(FriendlyByteBuf buffer){
        // Fill the buffer with packet
        RevervoxMod.LOGGER.info("ENCODING PACKET!");
        buffer.writeBoolean(this.state);
    }

    public static PrivacyModePacket decode(FriendlyByteBuf buffer){
        // Create the packet from the buffer
        RevervoxMod.LOGGER.info("DECODING PACKET!");
        return new PrivacyModePacket(buffer.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        // Handle the packet
        RevervoxMod.LOGGER.info("HANDLING PACKET!");
        ctx.get().enqueueWork(() -> {
            RevervoxMod.LOGGER.info("PACKET RECEIVED!!!!");
            if(ctx.get().getSender() != null){
                RevervoxVoicechatPlugin.setPrivacy(ctx.get().getSender().getUUID(), state);
                RevervoxMod.LOGGER.info("Set privacy mode for {} to {}", ctx.get().getSender().getName(), state);
            } else {
                RevervoxMod.LOGGER.warn("Received PrivacyModePacket without sender?");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
