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

public class ForgeEventBus {
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
    @SubscribeEvent
    public void tickEvent(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            RevervoxMod.TASKS.tick();
        }
    }
}
