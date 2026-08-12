package dev.omialien.revervoxmod.worldgen.dimension;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class RevervoxDimensions {
    public static final ResourceKey<LevelStem> HOUSE_0 = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(RevervoxMod.MOD_ID, "house_0"));
    public static final ResourceKey<Level> HOUSE_0_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(RevervoxMod.MOD_ID, "house_0"));
    public static final ResourceKey<LevelStem> HOUSE_1 = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(RevervoxMod.MOD_ID, "house_1"));
    public static final ResourceKey<Level> HOUSE_1_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(RevervoxMod.MOD_ID, "house_1"));
    public static final ResourceKey<LevelStem> HOUSE_2 = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(RevervoxMod.MOD_ID, "house_2"));
    public static final ResourceKey<Level> HOUSE_2_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(RevervoxMod.MOD_ID, "house_2"));
    public static final ResourceKey<LevelStem> PIT = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(RevervoxMod.MOD_ID, "pit"));
    public static final ResourceKey<Level> PIT_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(RevervoxMod.MOD_ID, "pit"));
    public static final ResourceKey<DimensionType> NIGHTMARE_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(RevervoxMod.MOD_ID, "nightmare_type"));

    public static final List<ResourceKey<Level>> HOUSE_LEVELS =
            List.of(HOUSE_0_LEVEL_KEY, HOUSE_1_LEVEL_KEY, HOUSE_2_LEVEL_KEY);

    public static final int R_XZ   = 24;// 48x48
    public static final int DOWN_Y = 4;
    public static final int UP_Y   = 16;
    public static final Vec3i SIZE = new Vec3i(R_XZ * 2, DOWN_Y + UP_Y, R_XZ * 2);

    public static final BlockPos PASTE = new BlockPos(0, 64, 0);
    public static final BlockPos PIT_SPAWN = new BlockPos(0, 64, 0);

    public static BlockPos houseSpawn() {
        return PASTE.offset(R_XZ, DOWN_Y, R_XZ);
    }

    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(NIGHTMARE_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000), false, false, false, false, 1.0,
                false,
                false, 0, 256, 256,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                0.1f,
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        Holder<DimensionType> type = types.getOrThrow(NIGHTMARE_DIM_TYPE);
        Holder<Biome> voidBiome = biomes.getOrThrow(Biomes.THE_VOID);

        context.register(HOUSE_0, makeVoidStem(type, voidBiome));
        context.register(HOUSE_1, makeVoidStem(type, voidBiome));
        context.register(HOUSE_2, makeVoidStem(type, voidBiome));
        context.register(PIT, makeVoidStem(type, voidBiome));
    }

    private static LevelStem makeVoidStem(Holder<DimensionType> type, Holder<Biome> biome) {
        FlatLevelGeneratorSettings flat =
                new FlatLevelGeneratorSettings(Optional.empty(), biome, List.of());
        flat.getLayersInfo().clear();
        flat.updateLayers();
        return new LevelStem(type, new FlatLevelSource(flat));
    }
}
