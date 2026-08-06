package dev.omialien.revervoxmod.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;

public class GuanoRockFeature extends Feature<NoneFeatureConfiguration> {

    private static final List<ResourceLocation> PIECES = List.of(
            new ResourceLocation(RevervoxMod.MOD_ID, "guano_rock_1"),
            new ResourceLocation(RevervoxMod.MOD_ID, "guano_rock_2"),
            new ResourceLocation(RevervoxMod.MOD_ID, "guano_rock_small_1"),
            new ResourceLocation(RevervoxMod.MOD_ID, "guano_rock_small_2"),
            new ResourceLocation(RevervoxMod.MOD_ID, "guano_rock_small_3")
    );

    public GuanoRockFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        var random = context.random();

        ResourceLocation chosen = PIECES.get(random.nextInt(PIECES.size()));

        StructureTemplateManager templateManager = level.getLevel().getServer().getStructureManager();
        Optional<StructureTemplate> templateOpt = templateManager.get(chosen);
        if (templateOpt.isEmpty()) {
            return false;
        }
        StructureTemplate template = templateOpt.get();

        BlockPos placePos = findValidFloor(level, origin);
        if (placePos == null) {
            return false;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.getRandom(random))
                .setRandom(random);

        template.placeInWorld(level, placePos, placePos, settings, random, 2);
        return true;
    }

    private BlockPos findValidFloor(WorldGenLevel level, BlockPos start) {
        int minY = level.getMinBuildHeight() + 1;
        BlockPos.MutableBlockPos pos = start.mutable();

        for (int y = start.getY(); y > minY; y--) {
            pos.setY(y);
            boolean currentIsAir = level.isEmptyBlock(pos);
            boolean belowIsSolid = !level.isEmptyBlock(pos.below());
            boolean aboveIsAir = level.isEmptyBlock(pos.above());

            if (currentIsAir && belowIsSolid && aboveIsAir) {
                return pos.below().immutable();
            }
        }
        return null;
    }
}
