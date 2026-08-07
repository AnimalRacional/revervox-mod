package dev.omialien.revervoxmod.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class RangeMeleeAttackGoal extends MeleeAttackGoal {
    protected double range;

    protected double loseRange;
    public RangeMeleeAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, double range, double loseRange) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.range = range*range;
        this.loseRange = loseRange*loseRange;
    }

    @Override
    public boolean canUse() {
        LivingEntity ent = this.mob.getTarget();
        if (ent == null) { return false; }
        return ent.distanceToSqr(this.mob.getEyePosition()) <= range && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity ent = this.mob.getTarget();
        if (ent == null) { return false; }
        return ent.distanceToSqr(this.mob.getEyePosition()) <= loseRange && super.canContinueToUse();
    }
}
