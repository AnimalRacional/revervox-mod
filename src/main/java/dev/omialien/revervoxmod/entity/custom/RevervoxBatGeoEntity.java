package dev.omialien.revervoxmod.entity.custom;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.goals.TargetSpokeGoal;
import dev.omialien.revervoxmod.particle.ParticleManager;
import dev.omialien.revervoxmod.registries.ParticleRegistry;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import dev.omialien.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import dev.omialien.revervoxmod.voicechat.audio.AudioEffect;
import dev.omialien.revervoxmod.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.UUID;

public class RevervoxBatGeoEntity extends FlyingMob implements GeoEntity, NeutralMob, HearingEntity, SpeakingEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private AudioPlayer currentAudioPlayer;
    @Nullable
    private UUID persistentAngerTarget;
    @Nullable
    private BlockPos targetPosition;
    public RevervoxBatGeoEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RVBatSweepAttackGoal());
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 3.0F));
        this.targetSelector.addGoal(1, new TargetSpokeGoal<>(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new RVHurtByTargetGoal(this, Player.class));
        super.registerGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 3D)
                .add(Attributes.FLYING_SPEED, 2.5D)
                .add(Attributes.ATTACK_SPEED, 1.8D);
    }

    @Override
    public int getCurrentSwingDuration() {
        int ANIMATION_TICKS = 13;
        int TRANSITION_TICKS = 5;
        int IDK_TICKS = 1;
        return ANIMATION_TICKS + TRANSITION_TICKS + IDK_TICKS;
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return pDistanceToClosestPlayer > 200.0D;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime) {
        this.remainingPersistentAngerTime = pRemainingPersistentAngerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pPersistentAngerTarget) {
        this.persistentAngerTarget = pPersistentAngerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<RevervoxBatGeoEntity> controller = new AnimationController<>(this, "Attack", 5, state -> {
            if (this.swinging)
                return state.setAndContinue(DefaultAnimations.ATTACK_BITE);

            state.getController().forceAnimationReset();

            return PlayState.STOP;
        });
        controller.triggerableAnim("attack.bite", DefaultAnimations.ATTACK_BITE);
        controllers.add(DefaultAnimations.genericFlyIdleController(this).transitionLength(5),
                controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void awardKillScore(@NotNull Entity pKilled, int pScoreValue, @NotNull DamageSource pSource) {
        if(pKilled instanceof Player player && RevervoxMod.vcApi instanceof VoicechatServerApi api){
            playPlayerAudio(player, api, createLocationalAudioChannel(api), new AudioEffect().setPitchEnabled(1.7f));
            this.remove(Entity.RemovalReason.DISCARDED);
        }
        super.awardKillScore(pKilled, pScoreValue, pSource);
    }

    private AudioChannel createLocationalAudioChannel(VoicechatServerApi api){
        Vec3 loc = this.getEyePosition();
        AudioChannel channel = api.createLocationalAudioChannel(UUID.randomUUID(), api.fromServerLevel(this.level()), api.createPosition(loc.x, loc.y, loc.z));
        if(channel == null){
            RevervoxMod.LOGGER.error("Couldn't create disappearing channel");
            return null;
        }
        channel.setCategory(RevervoxVoicechatPlugin.REVERVOX_CATEGORY);
        return channel;
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        VoicechatServerApi api = (VoicechatServerApi) RevervoxMod.vcApi;
        short[] audio = RevervoxVoicechatPlugin.getRandomAudio(true);
        if (audio != null) {
            playAudio(audio, api, createLocationalAudioChannel(api), new AudioEffect().setPitchEnabled(1.5f).setReverbEnabled(0.5f, 160, 3));
        }
        super.remove(pReason);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        ParticleManager.addParticlesAroundSelf(ParticleRegistry.REVERVOX_PARTICLES.get(), 1, this);
    }

    @Override
    public boolean isSpeakingAtMe(Player player) {
        if (this.hasLineOfSight(player)){
            if (RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()) != null){
                return RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()).isSpeaking();
            }
        }
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        if (pDamageSource.is(DamageTypes.FALL)){
            return null;
        }
        return SoundRegistry.REVERVOX_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        return this.random.nextInt(4) != 0 ? null : SoundRegistry.REVERVOX_BAT_IDLE.get();
    }

    @Override
    public AudioPlayer getCurrentAudioPlayer() {
        return this.currentAudioPlayer;
    }

    @Override
    public void setCurrentAudioPlayer(AudioPlayer player) {
        this.currentAudioPlayer = player;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        boolean result = super.doHurtTarget(target);
        // Trigger the GeckoLib attack animation
        triggerAnim("Attack", "attack.bite");
        return result;
    }

    public void tick() {
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getTarget() == null) {
            if (this.targetPosition != null && (!this.level().isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level().getMinBuildHeight())) {
                this.targetPosition = null;
            }

            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0D)) {
                this.targetPosition = BlockPos.containing(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0D, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
            }

            moveToTarget();
        }
    }

    public void moveToTarget(){
        BlockPos blockpos = this.targetPosition;
        if (blockpos != null) {
            double d2 = (double)blockpos.getX() + 0.5D - this.getX();
            double d0 = (double)blockpos.getY() + 0.1D - this.getY();
            double d1 = (double)blockpos.getZ() + 0.5D - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = vec3.add((Math.signum(d2) * 0.5D - vec3.x) * (double)0.1F, (Math.signum(d0) * (double)0.7F - vec3.y) * (double)0.1F, (Math.signum(d1) * 0.5D - vec3.z) * (double)0.1F);
            this.setDeltaMovement(vec31);
            float f = (float)(Mth.atan2(vec31.z, vec31.x) * (double)(180F / (float)Math.PI)) - 90.0F;
            float f1 = Mth.wrapDegrees(f - this.getYRot());
            this.zza = 0.5F;
            this.setYRot(this.getYRot() + f1);
        }
    }


    @Override
    public void onSpeak(long audioDuration) {
    }

    static class RVHurtByTargetGoal extends TargetGoal {

        private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        private int timestamp;
        private final Class<?>[] toTarget;
        public RVHurtByTargetGoal(RevervoxBatGeoEntity pMob, Class<?>... target) {
            super(pMob, true);
            this.toTarget = target;
        }

        public boolean canUse() {
            int i = this.mob.getLastHurtByMobTimestamp();
            LivingEntity livingentity = this.mob.getLastHurtByMob();
            if (i != this.timestamp && livingentity != null) {
                if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                    return false;
                } else {
                    for(Class<?> oclass : toTarget) {
                        if (oclass.isAssignableFrom(livingentity.getClass())) {
                            return this.canAttack(livingentity, HURT_BY_TARGETING);
                        }
                    }

                    return false;
                }
            } else {
                return false;
            }
        }
        public void start() {
            this.mob.setTarget(this.mob.getLastHurtByMob());
            this.targetMob = this.mob.getTarget();
            this.timestamp = this.mob.getLastHurtByMobTimestamp();
            this.unseenMemoryTicks = 300;

            super.start();
        }
    }

    class RVBatSweepAttackGoal extends Goal{
        private int attackCooldown = 0; // in ticks
        public RVBatSweepAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return RevervoxBatGeoEntity.this.getTarget() != null;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            LivingEntity livingentity = RevervoxBatGeoEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player player) {
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                return this.canUse();
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            RevervoxBatGeoEntity.this.setTarget(null);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = RevervoxBatGeoEntity.this.getTarget();
            if (livingentity != null) {
                Vec3i moveTargetPoint = new Vec3i((int) Math.round(livingentity.getX()), (int) Math.round(livingentity.getY(0.5D)), (int) Math.round(livingentity.getZ()));
                RevervoxBatGeoEntity.this.targetPosition = new BlockPos(moveTargetPoint);
                moveToTarget();

                if (attackCooldown <= 0 && RevervoxBatGeoEntity.this.getBoundingBox().inflate(0.5F, 0.0D, 0.5F).intersects(livingentity.getBoundingBox())) {

                    RevervoxBatGeoEntity.this.doHurtTarget(livingentity);

                    double attackSpeed = RevervoxBatGeoEntity.this.getAttributeValue(Attributes.ATTACK_SPEED);
                    attackCooldown = (int)(getCurrentSwingDuration() / attackSpeed);

                    //TODO som de ataque
                    if (!RevervoxBatGeoEntity.this.isSilent()) {
                        RevervoxBatGeoEntity.this.level().levelEvent(1039, RevervoxBatGeoEntity.this.blockPosition(), 0);
                    }
                }
                if (attackCooldown > 0) {
                    attackCooldown--;
                }
            }
        }
    }
}
