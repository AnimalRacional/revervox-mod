package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class FollowAngerLocationGoal extends Goal {
    private final RevervoxGeoEntity revervox;
    private Vec3 targetLocation;
    public FollowAngerLocationGoal(RevervoxGeoEntity revervox) {
        this.revervox = revervox;
    }

    @Override
    public boolean canUse() {
        if (this.revervox.hasAngerLocation()){
            RevervoxMod.LOGGER.debug("Revervox has anger location, starting...");
            this.targetLocation = this.revervox.getAngerLocation();
            this.revervox.setAngerLocation(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.revervox.position().distanceToSqr(targetLocation) < 1.0D;
    }

    @Override
    public void start() {
        this.revervox.getNavigation().moveTo( this.revervox.getNavigation().createPath(targetLocation.x, targetLocation.y, targetLocation.z, 0), this.revervox.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
        RevervoxMod.LOGGER.debug("Setting anger location to {}", targetLocation);
        super.start();
    }
}
