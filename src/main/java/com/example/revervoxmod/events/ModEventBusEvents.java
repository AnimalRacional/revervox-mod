package com.example.revervoxmod.events;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.entity.custom.ThingyEntity;
import com.example.revervoxmod.registries.EntityRegistry;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(EntityRegistry.THINGY.get(), ThingyEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX_GEO.get(), RevervoxGeoEntity.createAttributes().build());
    }
    @SubscribeEvent
    public static void registerSpawnPlacement(SpawnPlacementRegisterEvent event) {
        event.register(EntityRegistry.REVERVOX_GEO.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RevervoxGeoEntity::checkRevervoxSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
