package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;

public class RevervoxHurtByTargetGoal extends HurtByTargetGoal {
    public RevervoxHurtByTargetGoal(RevervoxGeoEntity revervox) {
        super(revervox);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !(this.mob.getTarget() instanceof Player);
    }

    @Override
    public void start() {
        super.start();
        if (this.mob.getTarget() instanceof Player) {
            ((RevervoxGeoEntity)this.mob).startPlaying();
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            livingentity = this.targetMob;
        }
        if (livingentity == null) {
            return false;
        }
        if (
                this.mob.getLastHurtByMobTimestamp() + RevervoxModServerConfigs.REVERVOX_FORGET_PAIN.get()
                        < this.mob.tickCount
                && this.mob.getLastHurtMobTimestamp() + RevervoxModServerConfigs.REVERVOX_FORGET_PAIN.get()
                        < this.mob.tickCount
        ) {
            if (this.mob instanceof RevervoxGeoEntity revervox) {
                revervox.mobLostTarget();
            }
            return false;
        }
        return super.canContinueToUse();
    }
}
