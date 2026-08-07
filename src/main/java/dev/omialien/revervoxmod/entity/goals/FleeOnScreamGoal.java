package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.voicechat.PlayerStateManager;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FleeOnScreamGoal extends Goal {
    protected PathfinderMob mob;
    protected PathNavigation pathNav;
    protected Player screaming = null;
    protected Path path = null;
    protected double walkSpeedModifier;
    protected double sprintSpeedModifier;
    public FleeOnScreamGoal(PathfinderMob mob, double walkSpeedModifier, double sprintSpeedModifier) {
        this.mob = mob;
        this.pathNav = mob.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
        this.walkSpeedModifier = walkSpeedModifier;
        this.sprintSpeedModifier = sprintSpeedModifier;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    @Override
    public boolean canUse() {
        this.screaming = this.mob.level().getNearestPlayer(this.mob, 64);
        if (this.screaming == null || !PlayerStateManager.isScreaming(this.screaming.getUUID())) {
            return false;
        } else {
            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.screaming.position());
            if (vec3 == null) {
                return false;
            } else if (this.screaming.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.screaming.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }
    }

    @Override
    public void start() {
        super.start();
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
        this.mob.setSprinting(true);
    }

    @Override
    public void stop() {
        this.screaming = null;
        this.mob.setSprinting(false);
    }

    @Override
    public void tick() {
        if (this.mob.distanceToSqr(this.screaming) < 49.0D) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }

    }
}
