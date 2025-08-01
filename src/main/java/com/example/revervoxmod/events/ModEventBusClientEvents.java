package com.example.revervoxmod.events;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.client.ModModelLayers;
import com.example.revervoxmod.entity.client.RevervoxGeoRenderer;
import com.example.revervoxmod.entity.client.ThingyModel;
import com.example.revervoxmod.entity.client.ThingyRenderer;
import com.example.revervoxmod.registries.EntityRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.THINGY_LAYER, ThingyModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityRegistry.THINGY.get(), ThingyRenderer::new);
        EntityRenderers.register(EntityRegistry.REVERVOX_GEO.get(), RevervoxGeoRenderer::new);
    }
}
