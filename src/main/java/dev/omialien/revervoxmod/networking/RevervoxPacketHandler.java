package dev.omialien.revervoxmod.networking;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.networking.packets.AddSoundInstancePacket;
import dev.omialien.revervoxmod.networking.packets.PrivacyModePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class RevervoxPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RevervoxMod.MOD_ID, "main"),
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
                DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> AddSoundInstancePacket::handle)
        );
    }
}
