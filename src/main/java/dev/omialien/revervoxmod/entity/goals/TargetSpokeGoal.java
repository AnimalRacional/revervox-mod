package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.HearingEntity;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.networking.packets.AddSoundInstancePacket;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Predicate;

public class TargetSpokeGoal<M extends Mob & HearingEntity & NeutralMob> extends NearestAttackableTargetGoal<Player> {
    protected LivingEntity target;
    private final M entity;
    private int aggroTime;
    private Player pendingTarget;
    private final SoundEvent soundToPlay;
    private final Predicate<LivingEntity> isAngerInducing;
    private final TargetingConditions startAggroTargetConditions;
    private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();

    public TargetSpokeGoal(M entity, Predicate<LivingEntity> pSelectionPredicate, SoundEvent soundToPlay) {
        super(entity, Player.class, 10, false, false, pSelectionPredicate);
        this.entity = entity;
        this.soundToPlay = soundToPlay;
        this.isAngerInducing = (player) -> {
            boolean isSpeakingAtMe = entity.isSpeakingAtMe((Player)player);
            boolean isAngryAt = entity.isAngryAt(player);
            boolean hasIndirectPassenger = entity.hasIndirectPassenger(player);
            //ExampleMod.LOGGER.debug("isSpeakingAtMe: " + isSpeakingAtMe + ", isAngryAt: " + isAngryAt + ", hasIndirectPassenger: " + hasIndirectPassenger);
            return (isSpeakingAtMe || isAngryAt) && !hasIndirectPassenger;
        };
        this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
    }
    public TargetSpokeGoal(M entity, Predicate<LivingEntity> pSelectionPredicate) {
        this(entity, pSelectionPredicate, null);
    }
    @Override
    public boolean canUse() {
        this.pendingTarget = this.entity.level().getNearestPlayer(this.startAggroTargetConditions, this.entity);
        return this.pendingTarget != null;
    }


    public void start() {
        this.aggroTime = this.adjustedTickDelay(5);
        if (this.soundToPlay != null){
            RevervoxPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.mob),
                    new AddSoundInstancePacket(this.mob.getId(), SoundRegistry.REVERVOX_ALERT.get(), SoundSource.HOSTILE));
        }
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
                this.entity.lookAt(this.pendingTarget, 10.0F, 10.0F);
                return true;
            }
        } else {
            if (this.target != null) {
                if (this.entity.hasIndirectPassenger(this.target)) {
                    return false;
                }

                if (this.continueAggroTargetConditions.test(this.entity, this.target)) {
                    return true;
                }
            }

            return super.canContinueToUse();
        }
    }

    public void tick() {

        if (this.entity.getTarget() == null) {
            super.setTarget(null);
        }
        if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
                this.target = this.pendingTarget;
                super.setTarget(this.target);
                RevervoxMod.LOGGER.debug("Target: " + this.target.getName());
                this.pendingTarget = null;
                super.start();
            }
        } else {
            super.tick();
        }

    }

}
