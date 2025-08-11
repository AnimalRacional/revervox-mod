package com.example.revervoxmod.entity.ai;

import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

import javax.annotation.Nullable;

public class RVClimbSqueezeNavigation extends MMPathNavigateGround {
    @Nullable
    private BlockPos pathToPosition;
    private final RevervoxGeoEntity revervox;
    public RVClimbSqueezeNavigation(RevervoxGeoEntity entity, Level level) {
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

    @Override
    public void tick() {

        if (level instanceof ServerLevel) {
            boolean isAboveSolid = level.getBlockState(this.revervox.blockPosition().above()).isSolid();
            boolean isTwoAboveSolid = level.getBlockState(this.revervox.blockPosition().above(2)).isSolid();
            boolean isThreeAboveSolid = level.getBlockState(this.revervox.blockPosition().above(3)).isSolid();

            Vec3i offset = this.revervox.getDirection().getNormal();
            boolean isFacingSolid = level.getBlockState(this.revervox.blockPosition().relative(this.revervox.getDirection())).isSolid();

            if (isFacingSolid) {
                offset = offset.offset(0, 1, 0);
            }

            boolean isOffsetFacingSolid = level.getBlockState(this.revervox.blockPosition().offset(offset)).isSolid();
            boolean isOffsetFacingAboveSolid = level.getBlockState(this.revervox.blockPosition().offset(offset).above()).isSolid();
            boolean isOffsetFacingTwoAboveSolid = level.getBlockState(this.revervox.blockPosition().offset(offset).above(2)).isSolid();

            boolean shouldCrouch = isTwoAboveSolid || (!isOffsetFacingSolid && !isOffsetFacingAboveSolid && (isOffsetFacingTwoAboveSolid || isFacingSolid && isThreeAboveSolid)) ;

            boolean shouldCrawl = isAboveSolid || !isOffsetFacingSolid && isOffsetFacingAboveSolid || isFacingSolid && isTwoAboveSolid;

            this.revervox.getEntityData().set(RevervoxGeoEntity.CROUCHING_ACCESSOR, shouldCrouch);
            this.revervox.setCrawling(shouldCrawl);
        }
        this.revervox.refreshDimensions();

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



}
