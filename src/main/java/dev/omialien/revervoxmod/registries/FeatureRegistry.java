package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.worldgen.feature.EchoTendrilFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FeatureRegistry {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, RevervoxMod.MOD_ID);

    public static final RegistryObject<EchoTendrilFeature> ECHO_TENDRIL_FEATURE =
            FEATURES.register("echo_tendril_feature", () -> new EchoTendrilFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}