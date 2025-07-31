package com.example.examplemod.entity.goals;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.custom.RevervoxGeoEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class TargetSpokeGoal extends NearestAttackableTargetGoal<Player> {
    protected LivingEntity target;
    private final RevervoxGeoEntity revervox;
    private int aggroTime;
    private Player pendingTarget;
    private final Predicate<LivingEntity> isAngerInducing;
    private final TargetingConditions startAggroTargetConditions;
    private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
    //private final Date createdTimestamp = new Date(System.currentTimeMillis());

    public TargetSpokeGoal(RevervoxGeoEntity revervox, Predicate<LivingEntity> pSelectionPredicate) {
        super(revervox, Player.class, 10, false, false, pSelectionPredicate);
        this.revervox = revervox;
        this.isAngerInducing = (player) -> {
            boolean isSpeakingAtMe = revervox.isSpeakingAtMe((Player)player);
            boolean isAngryAt = revervox.isAngryAt(player);
            boolean hasIndirectPassenger = revervox.hasIndirectPassenger(player);
            //ExampleMod.LOGGER.info("isSpeakingAtMe: " + isSpeakingAtMe + ", isAngryAt: " + isAngryAt + ", hasIndirectPassenger: " + hasIndirectPassenger);
            return (isSpeakingAtMe || isAngryAt) && !hasIndirectPassenger;
        };
        this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
    }
    @Override
    public boolean canUse() {
        this.pendingTarget = this.revervox.level().getNearestPlayer(this.startAggroTargetConditions, this.revervox);
        //ExampleMod.LOGGER.info("Pending Target: " + this.pendingTarget);
        return this.pendingTarget != null;
    }


    public void start() {
        this.aggroTime = this.adjustedTickDelay(5);
        super.start();
    }

    public void stop() {
        this.pendingTarget = null;
        super.stop();
    }

    public boolean canContinueToUse() {
        if (this.pendingTarget != null) {
            if (!this.isAngerInducing.test(this.pendingTarget)) {
                return false;
            } else {
                this.revervox.lookAt(this.pendingTarget, 10.0F, 10.0F);
                return true;
            }
        } else {
            if (this.target != null) {
                if (this.revervox.hasIndirectPassenger(this.target)) {
                    return false;
                }

                if (this.continueAggroTargetConditions.test(this.revervox, this.target)) {
                    return true;
                }
            }

            return super.canContinueToUse();
        }
    }

    public void tick() {

        if (this.revervox.getTarget() == null) {
            super.setTarget(null);
        }
        if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
                this.target = this.pendingTarget;
                super.setTarget(this.target);
                ExampleMod.LOGGER.info("Target: " + this.target.getName());
                this.pendingTarget = null;
                super.start();
            }
        } else {
            super.tick();
        }

    }

}
