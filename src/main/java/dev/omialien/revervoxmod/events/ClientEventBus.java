package dev.omialien.revervoxmod.events;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.client.*;
import dev.omialien.revervoxmod.particle.custom.RevervoxParticles;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.registries.ParticleRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = RevervoxMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RevervoxMod.MOD_ID, value = Dist.CLIENT)
public class ClientEventBus {
    public ClientEventBus(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.THINGY_LAYER, ThingyModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityRegistry.THINGY.get(), ThingyRenderer::new);
        EntityRenderers.register(EntityRegistry.REVERVOX.get(), RevervoxGeoRenderer::new);
        EntityRenderers.register(EntityRegistry.REVERVOX_BAT.get(), RevervoxBatGeoRenderer::new);
        EntityRenderers.register(EntityRegistry.REVERVOX_FAKE_BAT.get(), RevervoxFakeBatGeoRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.REVERVOX_PARTICLES.get(),
                RevervoxParticles.Provider::new);
    }
}
