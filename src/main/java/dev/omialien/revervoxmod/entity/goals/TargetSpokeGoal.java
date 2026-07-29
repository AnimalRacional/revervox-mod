package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.HearingEntity;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.networking.packets.SoundInstancePacket;
import dev.omialien.revervoxmod.registries.TriggerRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Predicate;

public class TargetSpokeGoal<M extends Mob & HearingEntity & NeutralMob> extends NearestAttackableTargetGoal<Player> {
    protected LivingEntity target;
    private final M entity;
    private int aggroTime;
    private Player pendingTarget;
    private final SoundEvent soundToPlay;
    private final SoundEvent soundToLoop;
    private final Predicate<LivingEntity> isAngerInducing;
    private final TargetingConditions startAggroTargetConditions;
    private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
    private boolean hasPlayedAudio = false;

    public TargetSpokeGoal(M entity, Predicate<LivingEntity> pSelectionPredicate, SoundEvent soundToPlay, SoundEvent soundToLoop, int range) {
        super(entity, Player.class, 10, false, false, pSelectionPredicate);
        this.entity = entity;
        this.soundToPlay = soundToPlay;
        this.soundToLoop = soundToLoop;
        this.isAngerInducing = (player) -> {
            boolean isSpeakingAtMe = entity.isSpeakingAtMe((Player)player);
            boolean isAngryAt = entity.isAngryAt(player);
            boolean hasIndirectPassenger = entity.hasIndirectPassenger(player);
            return (isSpeakingAtMe || isAngryAt) && !hasIndirectPassenger;
        };
        this.startAggroTargetConditions = TargetingConditions.forCombat().range(range).selector(this.isAngerInducing).ignoreLineOfSight();
    }
    public TargetSpokeGoal(M entity, Predicate<LivingEntity> pSelectionPredicate, SoundEvent soundToPlay, int range) {
        this(entity, pSelectionPredicate, soundToPlay, null, range);
    }
    public TargetSpokeGoal(M entity, Predicate<LivingEntity> pSelectionPredicate, int range) {
        this(entity, pSelectionPredicate, null, range);
    }
    @Override
    public boolean canUse() {
        this.pendingTarget = this.entity.level().getNearestPlayer(this.startAggroTargetConditions, this.entity);
        return this.pendingTarget != null && !(this.entity.getTarget() instanceof Player);
    }


    public void start() {
        if (!(this.mob.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.target != null) {
            double range = RevervoxModServerConfigs.REVERVOX_COOLDOWN_RANGE.get();
            if (range > 10000d) {
                RevervoxMod.COOLDOWN.markSpawn(level.players().stream().map(Entity::getUUID).iterator(), level.getGameTime());
            } else {
                Iterator<UUID> players = this.mob.level().getNearbyPlayers(
                        TargetingConditions.forNonCombat(),
                        null,
                        AABB.ofSize(this.mob.position(), range, range, range)
                ).stream().map(Entity::getUUID).iterator();
                RevervoxMod.COOLDOWN.markSpawn(players, level.getGameTime());
            }
        }
        this.aggroTime = this.adjustedTickDelay(5);
        if (this.soundToPlay != null){
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundToPlay, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
        if (this.soundToLoop != null){
            if (!hasPlayedAudio && this.mob instanceof RevervoxGeoEntity revervox) {
                revervox.startPlaying();
                hasPlayedAudio = true;
            } else {
                PacketDistributor.sendToPlayersTrackingEntity(
                        this.mob,
                        new SoundInstancePacket(
                                this.mob.getId(),
                                soundToLoop,
                                SoundSource.HOSTILE,
                                true
                        )
                );
            }
        }
        super.start();
    }

    public void stop() {
        this.pendingTarget = null;
        hasPlayedAudio = false;
        super.stop();
    }

    public boolean canContinueToUse() {
        if (this.pendingTarget != null) {
            if (!this.isAngerInducing.test(this.pendingTarget)) {
                if (this.target instanceof RevervoxGeoEntity revervox) {
                    revervox.mobLostTarget();
                }
                return false;
            } else {
                this.entity.lookAt(this.pendingTarget, 10.0F, 10.0F);
                return true;
            }
        } else {
            if (this.target != null) {
                if (this.entity.hasIndirectPassenger(this.target)) {
                    if (this.target instanceof RevervoxGeoEntity revervox) {
                        revervox.mobLostTarget();
                    }
                    return false;
                }
                if (this.target instanceof Player player && this.mob instanceof RevervoxGeoEntity revervox) {
                    if (!revervox.lastSpokeWithin(player, RevervoxModServerConfigs.REVERVOX_GIVE_UP_SILENT.get())) {
                        revervox.mobLostTarget();
                        return false;
                    }
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
                if(target instanceof ServerPlayer spTarget){
                    TriggerRegistry.HEARD_REVERVOX_TRIGGER.get().trigger(spTarget);
                }
            }
        } else {
            super.tick();
        }

    }

}
