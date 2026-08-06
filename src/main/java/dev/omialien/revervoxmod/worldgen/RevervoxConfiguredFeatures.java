package dev.omialien.revervoxmod.worldgen;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.registries.BlockRegistry;
import dev.omialien.revervoxmod.registries.FeatureRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class RevervoxConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ECHO_DARK_GRASS_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(RevervoxMod.MOD_ID, "echo_dark_grass"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> GUANO_ROCK_CONFIGURED_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(RevervoxMod.MOD_ID, "guano_rock_configured"));

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(ECHO_DARK_GRASS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simpleRandomPatchConfiguration(32,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(BlockRegistry.ECHO_DARK_GRASS.get()))))));
        context.register(
                GUANO_ROCK_CONFIGURED_KEY,
                new ConfiguredFeature<>(
                        FeatureRegistry.GUANO_ROCK_FEATURE.get(),
                        NoneFeatureConfiguration.INSTANCE
                )
        );
    }
}
