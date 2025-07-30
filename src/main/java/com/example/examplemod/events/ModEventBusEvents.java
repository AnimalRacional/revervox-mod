package com.example.examplemod.events;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.custom.RevervoxGeoEntity;
import com.example.examplemod.registries.EntityRegistry;
import com.example.examplemod.entity.custom.ThingyEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(EntityRegistry.THINGY.get(), ThingyEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX_GEO.get(), RevervoxGeoEntity.createAttributes().build());
    }

}
