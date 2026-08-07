package dev.omialien.revervoxmod.worldgen;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static dev.omialien.revervoxmod.worldgen.RevervoxConfiguredFeatures.ECHO_TENDRIL_CONFIGURED_KEY;

public class RevervoxPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ECHO_DARK_GRASS_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(RevervoxMod.MOD_ID, "echo_dark_grass_placed"));

    public static final ResourceKey<PlacedFeature> ECHO_TENDRIL_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(RevervoxMod.MOD_ID, "echo_tendril_placed"));
    public static final ResourceKey<PlacedFeature> ECHO_DARK_VEIN_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(RevervoxMod.MOD_ID, "echo_dark_vein_placed"));

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(ECHO_DARK_GRASS_PLACED_KEY, new PlacedFeature(
                configured.getOrThrow(RevervoxConfiguredFeatures.ECHO_DARK_GRASS_KEY),
                List.of(
                        CountPlacement.of(90),
                        InSquarePlacement.spread(),
                        PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                        EnvironmentScanPlacement.scanningFor(Direction.DOWN,
                                BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12),
                        RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                        BiomeFilter.biome()
                )));
        context.register(
                RevervoxPlacedFeatures.ECHO_TENDRIL_PLACED_KEY,
                new PlacedFeature(
                        configured.getOrThrow(ECHO_TENDRIL_CONFIGURED_KEY),
                        List.of(
                                CountPlacement.of(2),
                                InSquarePlacement.spread(),
                                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT
                        )
                )
        );
        PlacementUtils.register(
                context,
                ECHO_DARK_VEIN_PLACED_KEY,
                configured.getOrThrow(RevervoxConfiguredFeatures.ECHO_DARK_VEIN_KEY),
                CountPlacement.of(UniformInt.of(204, 250)),
                InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT);
    }
}
