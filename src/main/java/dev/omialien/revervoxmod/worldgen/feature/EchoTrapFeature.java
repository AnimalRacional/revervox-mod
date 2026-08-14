package dev.omialien.revervoxmod.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class EchoTrapFeature extends AbstractEchoDarkFeature{

    private static final int MAX_HEIGHT_VARIANCE = 1;

    public EchoTrapFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec, List.of(
                new ResourceLocation(RevervoxMod.MOD_ID, "trap_0"),
                new ResourceLocation(RevervoxMod.MOD_ID, "trap_1")
        ), MAX_HEIGHT_VARIANCE);
    }
}
