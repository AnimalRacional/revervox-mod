package dev.omialien.revervoxmod.events;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.RevervoxBatLayer;
import dev.omialien.revervoxmod.entity.client.*;
import dev.omialien.revervoxmod.items.TapeItem;
import dev.omialien.revervoxmod.particle.custom.RevervoxParticles;
import dev.omialien.revervoxmod.particle.custom.RevervoxSonicBoomParticle;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.ParticleRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBus {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.THINGY_LAYER, ThingyModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new RevervoxBatLayer(renderer));
            }
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityRegistry.THINGY.get(), ThingyRenderer::new);
        EntityRenderers.register(EntityRegistry.REVERVOX.get(), RevervoxGeoRenderer::new);
        EntityRenderers.register(EntityRegistry.REVERVOX_BAT.get(), RevervoxBatGeoRenderer::new);
        EntityRenderers.register(EntityRegistry.REVERVOX_FAKE_BAT.get(), RevervoxFakeBatGeoRenderer::new);
        EntityRenderers.register(EntityRegistry.STRIDORVOX.get(), StridorVoxGeoRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.REVERVOX_PARTICLES.get(),
                RevervoxParticles.Provider::new);
        event.registerSpriteSet(ParticleRegistry.REVERVOX_SONIC_BOOM_PARTICLES.get(), RevervoxSonicBoomParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((item, color) -> {
            CompoundTag tag = item.getTag();
            if (tag != null && tag.contains(TapeItem.PLAYER_ID)) {
                UUID player = tag.getUUID(TapeItem.PLAYER_ID);
                UUID audio = tag.getUUID(TapeItem.AUDIO_ID);
                int hash = Objects.hash(player, audio);
                if (color == 1){
                    return hash % 0xffffff;
                } else return -1;
            }
            return color == 1 ? 0 : -1;
        }, ItemRegistry.TAPE.get());
    }
}
