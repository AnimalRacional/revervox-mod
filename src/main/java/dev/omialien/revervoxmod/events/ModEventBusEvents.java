package dev.omialien.revervoxmod.events;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.datagen.RevervoxWorldGenProvider;
import dev.omialien.revervoxmod.datagen.RevervoxAdvancementProvider;
import dev.omialien.revervoxmod.entity.custom.*;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.worldgen.biome.surface.RevervoxSurfaceRules;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import terrablender.api.SurfaceRuleManager;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(EntityRegistry.THINGY.get(), ThingyEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX.get(), RevervoxGeoEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX_BAT.get(), RevervoxBatGeoEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX_FAKE_BAT.get(), RevervoxFakeBatEntity.createAttributes().build());
        event.put(EntityRegistry.STRIDORVOX.get(), StridorvoxEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD,
                    RevervoxMod.MOD_ID, RevervoxSurfaceRules.makeRules());
            RevervoxPacketHandler.registerPackets();
        });
    }


    @SubscribeEvent
    public static void registerSpawnPlacement(SpawnPlacementRegisterEvent event) {
        event.register(EntityRegistry.REVERVOX.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RevervoxGeoEntity::checkRevervoxSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void GatherDataEvent(GatherDataEvent event){
        RevervoxMod.LOGGER.debug("GATHERDATA");
        System.out.println("GATHERDATAln");
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeServer(), new RevervoxAdvancementProvider(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new RevervoxWorldGenProvider(gen.getPackOutput(), event.getLookupProvider()));
    }
}
