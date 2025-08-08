package com.example.revervoxmod.events;

import com.example.revervoxmod.RevervoxMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonForgeEventBus {
    @SubscribeEvent
    public void tickEvent(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            RevervoxMod.TASKS.tick();
        }
    }
}
