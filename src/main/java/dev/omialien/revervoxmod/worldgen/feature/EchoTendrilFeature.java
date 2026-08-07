package dev.omialien.revervoxmod.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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

public class EchoTendrilFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_HEIGHT_VARIANCE = 4;

    private static final List<ResourceLocation> PIECES = List.of(
            new ResourceLocation(RevervoxMod.MOD_ID, "echo_tendril_1"),
            new ResourceLocation(RevervoxMod.MOD_ID, "echo_tendril_2")
            );

    public EchoTendrilFeature(Codec<NoneFeatureConfiguration> codec) {
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
        Vec3i size = template.getSize();

        BlockPos placePos = findValidFlatFloor(level, origin, size);
        if (placePos == null) {
            return false;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.getRandom(random))
                .setRandom(random);

        template.placeInWorld(level, placePos, placePos, settings, random, 2);
        return true;
    }

    private BlockPos findValidFlatFloor(WorldGenLevel level, BlockPos start, Vec3i size) {
        int minY = level.getMinBuildHeight() + 1;
        int halfX = size.getX() / 2;
        int halfZ = size.getZ() / 2;

        BlockPos.MutableBlockPos pos = start.mutable();

        for (int y = start.getY(); y > minY; y--) {
            pos.setY(y);

            boolean currentIsAir = level.isEmptyBlock(pos);
            boolean aboveIsAir = level.isEmptyBlock(pos.above());
            boolean centerFloorIsTarget = isEchoDarkSurface(level, pos.below());

            if (!currentIsAir || !aboveIsAir || !centerFloorIsTarget) {
                continue;
            }

            if (isFootprintFlatAndValid(level, pos, halfX, halfZ)) {
                return pos.below().immutable();
            }
        }
        return null;
    }

    private boolean isFootprintFlatAndValid(WorldGenLevel level, BlockPos.MutableBlockPos center, int halfX, int halfZ) {
        int baseY = center.getY();

        int[][] offsets = {
                {-halfX, -halfZ}, {halfX, -halfZ},
                {-halfX, halfZ}, {halfX, halfZ}
        };

        for (int[] offset : offsets) {
            BlockPos checkPos = center.offset(offset[0], 0, offset[1]);
            BlockPos.MutableBlockPos mutable = checkPos.mutable();

            boolean foundValidCorner = false;
            for (int dy = -MAX_HEIGHT_VARIANCE; dy <= MAX_HEIGHT_VARIANCE; dy++) {
                mutable.setY(baseY + dy);
                boolean isAir = level.isEmptyBlock(mutable);
                boolean belowIsTarget = isEchoDarkSurface(level, mutable.below());

                if (isAir && belowIsTarget) {
                    foundValidCorner = true;
                    break;
                }
            }

            if (!foundValidCorner) {
                return false;
            }
        }
        return true;
    }

    private boolean isEchoDarkSurface(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockRegistry.ECHO_DARK_SURFACE_BLOCK.get());
    }
}
