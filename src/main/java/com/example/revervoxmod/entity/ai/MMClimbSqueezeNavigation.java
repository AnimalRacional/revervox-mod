package com.example.revervoxmod.entity.ai;

import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

import javax.annotation.Nullable;

public class MMClimbSqueezeNavigation extends MMPathNavigateGround {
    @Nullable
    private BlockPos pathToPosition;
    private final RevervoxGeoEntity revervox;
    public MMClimbSqueezeNavigation(RevervoxGeoEntity entity, Level level) {
        super(entity, level);
        this.revervox = entity;
    }
    public Path createPath(BlockPos pPos, int pAccuracy) {
        this.pathToPosition = pPos;
        return super.createPath(pPos, pAccuracy);
    }

    public Path createPath(Entity pEntity, int pAccuracy) {
        this.pathToPosition = pEntity.blockPosition();
        return super.createPath(pEntity, pAccuracy);
    }

    public boolean moveTo(Entity pEntity, double pSpeed) {
        Path path = this.createPath(pEntity, 0);
        if (path != null) {
            return this.moveTo(path, pSpeed);
        } else {
            this.pathToPosition = pEntity.blockPosition();
            this.speedModifier = pSpeed;
            return true;
        }
    }

    public enum PassageType {
        NONE,
        ONE_BY_TWO,
        ONE_BY_ONE
    }

    @Override
    public void tick() {
        BlockPos checkPos = null;

        if (this.path != null && !this.path.isDone()) {
            checkPos = this.path.getNextNodePos();
        } else if (this.pathToPosition != null) {
            checkPos = this.pathToPosition;
        }

        if (checkPos != null) {
            PassageType type = getPassageType(checkPos);
            revervox.setPassageType(type);
        } else {
            revervox.setPassageType(PassageType.NONE);
        }

        if (!this.isDone()) {
            super.tick();
        } else {
            if (revervox.isAngry()) {
                if (this.pathToPosition != null) {
                    if (!this.pathToPosition.closerToCenterThan(this.mob.position(),
                            Math.max(this.mob.getBbWidth(), 1.0D)) &&
                            (!(this.mob.getY() > (double)this.pathToPosition.getY()) ||
                                    !(BlockPos.containing(this.pathToPosition.getX(), this.mob.getY(), this.pathToPosition.getZ()))
                                            .closerToCenterThan(this.mob.position(),
                                                    Math.max(this.mob.getBbWidth(), 1.0D)))) {
                        this.mob.getMoveControl().setWantedPosition(this.pathToPosition.getX(),
                                this.pathToPosition.getY(),
                                this.pathToPosition.getZ(),
                                this.speedModifier);
                    } else {
                        this.pathToPosition = null;
                    }
                }
            }
        }
    }

    private PassageType getPassageType(BlockPos pos) {
        boolean emptyHere = level.isEmptyBlock(pos);
        boolean emptyAbove = level.isEmptyBlock(pos.above());
        boolean emptyTwoAbove = level.isEmptyBlock(pos.above(2));

        if (emptyHere && emptyAbove && !emptyTwoAbove) {
            return PassageType.ONE_BY_TWO;
        }
        if (emptyHere && !emptyAbove) {
            return PassageType.ONE_BY_ONE;
        }
        return PassageType.NONE;
    }


}
