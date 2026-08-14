package dev.omialien.revervoxmod.blocks;

import dev.omialien.revervoxmod.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EchoDarkVegetationBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 14, 14);
    public EchoDarkVegetationBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockRegistry.ECHO_DARK_SURFACE_BLOCK.get());
    }
}
