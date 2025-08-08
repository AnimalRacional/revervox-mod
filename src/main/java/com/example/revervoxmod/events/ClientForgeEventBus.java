package com.example.revervoxmod.events;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.config.RevervoxModClientConfigs;
import com.example.revervoxmod.networking.RevervoxPacketHandler;
import com.example.revervoxmod.networking.packets.PrivacyModePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ClientForgeEventBus {
    @SubscribeEvent
    public void clientJoinEvent(EntityJoinLevelEvent event){
            if(event.getLevel().isClientSide() &&
                    event.getEntity() instanceof LocalPlayer){
                Minecraft.getInstance().submit(() -> {
                    RevervoxMod.LOGGER.info("CLIENT SENDING ENTITY JOIN PACKET!");
                    RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
                });
            }
    }

    // \/\/\/ TODO descobrir porque nenhum destes eventos é chamado... talvez sejam para a server-side config?
    @SubscribeEvent
    public void clientConfigChangedEvent(ModConfigEvent.Loading event){
        RevervoxMod.LOGGER.info("CONFIG CHANGED! load");
        if(event.getConfig().getModId().equals(RevervoxMod.MOD_ID)){
            Minecraft.getInstance().submit(() -> {
                RevervoxMod.LOGGER.info("CLIENT SENDING CONFIG CHANGED PACKET!");
                RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
            });
        }
    }

    @SubscribeEvent
    public void clientConfigChangedEvent(ModConfigEvent.Unloading event){
        RevervoxMod.LOGGER.info("CONFIG CHANGED! unload");
        if(event.getConfig().getModId().equals(RevervoxMod.MOD_ID)){
            Minecraft.getInstance().submit(() -> {
                RevervoxMod.LOGGER.info("CLIENT SENDING CONFIG CHANGED PACKET!");
                RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
            });
        }
    }

    @SubscribeEvent
    public void clientConfigChangedEvent(ModConfigEvent.Reloading event){
        RevervoxMod.LOGGER.info("CONFIG CHANGED! reload");
        if(event.getConfig().getModId().equals(RevervoxMod.MOD_ID)){
            Minecraft.getInstance().submit(() -> {
                RevervoxMod.LOGGER.info("CLIENT SENDING CONFIG CHANGED PACKET!");
                RevervoxPacketHandler.INSTANCE.sendToServer(new PrivacyModePacket(RevervoxModClientConfigs.PRIVACY_MODE.get()));
            });
        }
    }
    /// ^^^
}
