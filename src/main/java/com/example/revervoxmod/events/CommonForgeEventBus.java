package com.example.revervoxmod.events;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.config.RevervoxModServerConfigs;
import com.example.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import com.example.revervoxmod.registries.EntityRegistry;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class CommonForgeEventBus {
    @SubscribeEvent
    public void tickEvent(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            RevervoxMod.TASKS.tick();
        }
    }

    @SubscribeEvent
    public void mobSpawnEvent(MobSpawnEvent.FinalizeSpawn event){
        if (event.getEntity() instanceof Bat) {
            if (new Random().nextInt(RevervoxModServerConfigs.REVERVOX_BAT_SPAWN_CHANCE.get()) == 0){
                RevervoxBatGeoEntity bat = new RevervoxBatGeoEntity(EntityRegistry.REVERVOX_BAT.get(), event.getLevel().getLevel());
                bat.moveTo(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
                RevervoxMod.LOGGER.info("Spawning Revervox Bat! at " + event.getEntity().getX() + ", " + event.getEntity().getY() + ", " + event.getEntity().getZ());
                event.setSpawnCancelled(true);
                event.getLevel().addFreshEntity(bat);
            }
        }
    }
}
