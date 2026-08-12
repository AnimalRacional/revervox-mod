package dev.omialien.revervoxmod.datagen;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.worldgen.RevervoxConfiguredFeatures;
import dev.omialien.revervoxmod.worldgen.RevervoxPlacedFeatures;
import dev.omialien.revervoxmod.worldgen.biome.RevervoxBiomes;
import dev.omialien.revervoxmod.worldgen.dimension.RevervoxDimensions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RevervoxWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, RevervoxDimensions::bootstrapType)
            .add(Registries.CONFIGURED_FEATURE, RevervoxConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, RevervoxPlacedFeatures::bootstrap)
            .add(Registries.BIOME, RevervoxBiomes::bootstrap)
            .add(Registries.LEVEL_STEM, RevervoxDimensions::bootstrapStem);

    public RevervoxWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(RevervoxMod.MOD_ID));
    }
}
