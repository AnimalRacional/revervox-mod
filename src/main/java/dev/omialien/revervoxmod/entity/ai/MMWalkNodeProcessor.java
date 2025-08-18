package dev.omialien.revervoxmod.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

//Code is from forge version of Mowzie's mobs: https://github.com/BobMowzie/MowziesMobs
public class MMWalkNodeProcessor extends WalkNodeEvaluator {
    private final Level level;
    public MMWalkNodeProcessor(Level level){
        this.level = level;
    }
    @Override
    @NotNull
    public Node getStart() {
        int y;
        AABB bounds = this.mob.getBoundingBox();
        if (this.mob.canStartSwimming() && this.mob.isUnderWater()) {
            y = (int) bounds.minY;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(this.mob.getX()), y, Mth.floor(this.mob.getZ()));
            for (Block block = this.level.getBlockState(pos).getBlock(); block == Blocks.WATER; block = this.level.getBlockState(pos).getBlock()) {
                pos.setY(++y);
            }
        } else if (this.mob.onGround()) {
            y = Mth.floor(bounds.minY + 0.5D);
        } else {
            y = Mth.floor(this.mob.getY());
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(this.mob.getX()), y, Mth.floor(this.mob.getZ()));
            BlockState blockState = this.level.getBlockState(pos);
            while (y > 0 && (blockState.isAir() ||
                    // TODO changed in neoforge port - is this correct?
                    blockState.getCollisionShape(
                            this.currentContext.level(),
                            pos
                    ) != Shapes.empty()
                            )) {
                pos.setY(y--);
            }
            y++;
        }
        // account for node size
        float r = this.mob.getBbWidth() * 0.5F;
        int x = Mth.floor(this.mob.getX() - r);
        int z = Mth.floor(this.mob.getZ() - r);
        if (this.mob.getPathfindingMalus(this.getPathType(this.mob, new BlockPos(x,y,z))) < 0.0F) {
            Set<BlockPos> diagonals = new HashSet<>();
            diagonals.add(new BlockPos((int) (bounds.minX - r), y, (int) (bounds.minZ - r)));
            diagonals.add(new BlockPos((int) (bounds.minX - r), y, (int) (bounds.maxZ - r)));
            diagonals.add(new BlockPos((int) (bounds.maxX - r), y, (int) (bounds.minZ - r)));
            diagonals.add(new BlockPos((int) (bounds.maxX - r), y, (int) (bounds.maxZ - r)));
            for (BlockPos p : diagonals) {
                PathType pathnodetype = this.getPathType(this.mob, new BlockPos(p.getX(), p.getY(), p.getZ()));
                if (this.mob.getPathfindingMalus(pathnodetype) >= 0.0F) {
                    return this.getNode(p.getX(), p.getY(), p.getZ());
                }
            }
        }
        return this.getNode(x, y, z);
    }
}
