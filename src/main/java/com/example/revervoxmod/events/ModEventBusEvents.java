package com.example.revervoxmod.events;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.entity.custom.ThingyEntity;
import com.example.revervoxmod.registries.EntityRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(EntityRegistry.THINGY.get(), ThingyEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX_GEO.get(), RevervoxGeoEntity.createAttributes().build());
    }
}
