package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.worldgen.feature.GuanoRockFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FeatureRegistry {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, RevervoxMod.MOD_ID);

    public static final RegistryObject<GuanoRockFeature> GUANO_ROCK_FEATURE =
            FEATURES.register("guano_rock_feature", () -> new GuanoRockFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}