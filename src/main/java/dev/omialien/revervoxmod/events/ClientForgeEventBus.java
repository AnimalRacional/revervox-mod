package dev.omialien.revervoxmod.events;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModClientConfigs;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.networking.packets.PrivacyModePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@OnlyIn(Dist.CLIENT)
public class ClientForgeEventBus {
    @SubscribeEvent
    public void clientJoinEvent(EntityJoinLevelEvent event){
            if(event.getLevel().isClientSide() &&
                    event.getEntity() instanceof LocalPlayer){
                Minecraft.getInstance().submit(() -> {
                    RevervoxMod.LOGGER.debug("Client Sending EntityJoinLevelEvent Packet!");
                    RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
                });
            }
    }

    // \/\/\/ TODO descobrir porque nenhum destes eventos é chamado... talvez sejam para a server-side config?
    @SubscribeEvent
    public void clientConfigChangedEvent(ModConfigEvent.Loading event){
        RevervoxMod.LOGGER.debug("Client Config Changed! Load");
        if(event.getConfig().getModId().equals(RevervoxMod.MOD_ID)){
            Minecraft.getInstance().submit(() -> {
                RevervoxMod.LOGGER.debug("Client Sending Config Changed Packet!");
                RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
            });
        }
    }

    @SubscribeEvent
    public void clientConfigChangedEvent(ModConfigEvent.Unloading event){
        RevervoxMod.LOGGER.debug("Client Config Changed! Unload");
        if(event.getConfig().getModId().equals(RevervoxMod.MOD_ID)){
            Minecraft.getInstance().submit(() -> {
                RevervoxMod.LOGGER.debug("Client Sending Config Changed Packet!");
                RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
            });
        }
    }

    @SubscribeEvent
    public void clientConfigChangedEvent(ModConfigEvent.Reloading event){
        RevervoxMod.LOGGER.debug("Client Config Changed! Reload");
        if(event.getConfig().getModId().equals(RevervoxMod.MOD_ID)){
            Minecraft.getInstance().submit(() -> {
                RevervoxMod.LOGGER.debug("Client Sending Config Changed Packet!");
                RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
            });
        }
    }
    /// ^^^
}
