package com.example.revervoxmod.networking;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.networking.packets.AddSoundInstancePacket;
import com.example.revervoxmod.networking.packets.PrivacyModePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class RevervoxPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets(){
        int id = 0;
        INSTANCE.registerMessage(
                id++,
                PrivacyModePacket.class,
                PrivacyModePacket::encode,
                PrivacyModePacket::decode,
                PrivacyModePacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                AddSoundInstancePacket.class,
                AddSoundInstancePacket::encode,
                AddSoundInstancePacket::decode,
                AddSoundInstancePacket::handle
        );
    }
}
