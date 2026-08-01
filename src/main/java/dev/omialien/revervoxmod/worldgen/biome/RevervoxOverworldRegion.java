package dev.omialien.revervoxmod.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class RevervoxOverworldRegion extends Region {

    public RevervoxOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        /*
        // EASY WAY: adds a new region and replaces a biome with a new one
        super.addModifiedVanillaOverworldBiomes(mapper, modifiedVanillaOverworldBuilder -> {
            modifiedVanillaOverworldBuilder.replaceBiome(Biomes.DEEP_DARK, RevervoxBiomes.REVERVOX_BIOME);
        });
         */

        // PROPER WAY:
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        // Overlap Vanilla's parameters with our own for our REVERVOX biome.
        // The parameters for this biome are chosen arbitrarily.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(ParameterUtils.Temperature.FROZEN, ParameterUtils.Temperature.COOL))
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.DRY))
                .continentalness(ParameterUtils.Continentalness.INLAND)
                .erosion(ParameterUtils.Erosion.EROSION_4)
                .depth(Climate.Parameter.point(0.95F)) // Biome height that it can generate
                .weirdness(ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING) // Separate into blobs
                .offset(0.16F) // Biome global size
                .build().forEach(point -> builder.add(point, RevervoxBiomes.REVERVOX_BIOME));

        // Add our points to the mapper
        builder.build().forEach(mapper);

    }
}
