package com.example.revervoxmod.entity.custom;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.config.RevervoxModServerConfigs;
import com.example.revervoxmod.entity.ai.MMEntityMoveHelper;
import com.example.revervoxmod.entity.ai.RVClimbNavigation;
import com.example.revervoxmod.entity.goals.RandomRepeatGoal;
import com.example.revervoxmod.entity.goals.RevervoxHurtByTargetGoal;
import com.example.revervoxmod.entity.goals.TargetSpokeGoal;
import com.example.revervoxmod.registries.ParticleRegistry;
import com.example.revervoxmod.registries.SoundRegistry;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.voicechat.audio.AudioEffect;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class RevervoxGeoEntity extends Monster implements GeoEntity, NeutralMob, HearingEntity, SpeakingEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Boolean> CLIMBING_ACCESSOR = SynchedEntityData.defineId(RevervoxGeoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(50, 60);
    private int remainingPersistentAngerTime;
    private long firstSpeak;
    private static final long NOT_SPOKEN_YET = -1;
    private AudioPlayer currentAudioPlayer;

    @Nullable
    private UUID persistentAngerTarget;

    public RevervoxGeoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        moveControl = new MMEntityMoveHelper(this, 90);
        firstSpeak = NOT_SPOKEN_YET;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CLIMBING_ACCESSOR, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this).transitionLength(5),
                DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_SWING).transitionLength(5));
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.addParticlesAroundSelf(ParticleRegistry.REVERVOX_PARTICLES.get(), 2);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    @Override
    protected void registerGoals() {
        RevervoxMod.LOGGER.info("(!!!) Revervox Spawned");
        // So it doesn't sink in the water
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RandomRepeatGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, Ingredient.of(Items.MUSIC_DISC_13), false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.addBehaviourGoals();

    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.7D, false));
        this.targetSelector.addGoal(1, new TargetSpokeGoal<>(this, this::isAngryAt, SoundRegistry.REVERVOX_ALERT.get()));
        this.targetSelector.addGoal(2, new RevervoxHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public float getSpeed() {
        if (this.isSwimming()){
            return 5.0F;
        } else{
            return super.getSpeed();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 14D)
                .add(Attributes.ATTACK_SPEED, 0.3D);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        RVClimbNavigation navigation = new RVClimbNavigation(this, pLevel);
        navigation.setMaxVisitedNodesMultiplier(4);
        return navigation;
    }

    @Override
    public int getCurrentSwingDuration() {
        int ANIMATION_TICKS = 5;
        int TRANSITION_TICKS = 5;
        int IDK_TICKS = 1;
        return ANIMATION_TICKS + TRANSITION_TICKS + IDK_TICKS;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int pTime) {
        this.remainingPersistentAngerTime = pTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }
    public void setPersistentAngerTarget(@Nullable UUID pTarget) {
        this.persistentAngerTarget = pTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        RevervoxMod.LOGGER.debug("Starting persistent anger timer!");
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.REVERVOX_DEATH.get();
    }
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource dmgSrc) {
        return SoundRegistry.REVERVOX_HURT.get();
    }
    @Override
    public void onKilledPlayer(Player player, VoicechatServerApi api){
        Vec3 loc = this.getEyePosition();
        AudioChannel channel = api.createLocationalAudioChannel(UUID.randomUUID(), api.fromServerLevel(player.getCommandSenderWorld()), api.createPosition(loc.x, loc.y, loc.z));
        if(channel == null){
            RevervoxMod.LOGGER.error("Couldn't create disappearing channel");
            return;
        }
        channel.setCategory(RevervoxVoicechatPlugin.REVERVOX_CATEGORY);
        playPlayerAudio(player, api, channel, new AudioEffect().setPitchEnabled(0.7f));
        this.remove(Entity.RemovalReason.DISCARDED);
    }

    public boolean hasSpoken(){
        return firstSpeak != NOT_SPOKEN_YET;
    }

    public long getFirstSpoken(){
        return firstSpeak;
    }

    public void onSpeak(long audioDuration){
        if(!hasSpoken() && !level().isClientSide()){
            firstSpeak = System.currentTimeMillis() + audioDuration;
        }
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
    public boolean isSpeakingAtMe(Player player) {
        long time = System.currentTimeMillis();
        long diff = time - getFirstSpoken();
        if(hasSpoken() && diff >= getGracePeriod()){
            if (RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()) != null){
                return RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()).isSpeaking();
            } else return false;
        }
        return false;
    }

    public boolean teleportTowards(Entity pTarget) {
        Vec3 vec3 = new Vec3(this.getX() - pTarget.getX(), this.getY(0.5D) - pTarget.getEyeY(), this.getZ() - pTarget.getZ());
        vec3 = vec3.normalize();
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * 16.0D;
        double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3.y * 16.0D;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * 16.0D;
        return this.teleport(d1, d2, d3);
    }


    private boolean teleport(double pX, double pY, double pZ) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(pX, pY, pZ);

        while(blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).isSolidRender(this.level(), blockpos$mutableblockpos)) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.isSolidRender(this.level(), blockpos$mutableblockpos);
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        //RevervoxMod.LOGGER.debug("flag: " + flag + " flag1: " + flag1);
        if (flag && !flag1) {
            //RevervoxMod.LOGGER.debug("Entered main if statement");
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, pX, pY, pZ);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            //RevervoxMod.LOGGER.debug("flag2: " + flag2);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.GLASS_STEP, this.getSoundSource(), 1.0F, 1.0F);
                    //TODO play a random minecraft sound to scare.
                    this.playSound(SoundEvents.GLASS_STEP, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }
    }

    @Override
    public void setTarget(@org.jetbrains.annotations.Nullable LivingEntity pTarget) {
        if (!this.level().isClientSide){
            if (pTarget == null && this.getTarget() != null) {
                if (this.getTarget() instanceof Player player){
                    this.onKilledPlayer(player, (VoicechatServerApi) RevervoxMod.vcApi);
                }
            }
        }
        super.setTarget(pTarget);
    }
    @Override
    public void updatePersistentAnger(ServerLevel pServerLevel, boolean pUpdateAnger) {
        if (this.getTarget() == null || !this.hasLineOfSight(this.getTarget())) {
            NeutralMob.super.updatePersistentAnger(pServerLevel, pUpdateAnger);
        }
    }

    @Override
    protected void customServerAiStep() {
        if (this.getTarget() != null && !this.hasLineOfSight(this.getTarget())) {
            double playerDirectionOffset = (this.getTarget().getY() - this.getY());
            double offset = Double.compare(playerDirectionOffset, 0.0D);
            offset = offset < 0.0D ? -1.0D : offset == 0 ? 0.0D : 1.0D;
            boolean inWall = this.checkWalls(this.getBoundingBox().inflate(0.4D, 0, 0.2D).move(0, offset, 0));
        }

        super.customServerAiStep();
    }

    private boolean checkWalls(AABB pArea) {
        int i = Mth.floor(pArea.minX);
        int j = Mth.floor(pArea.minY);
        int k = Mth.floor(pArea.minZ);
        int l = Mth.floor(pArea.maxX);
        int i1 = Mth.floor(pArea.maxY);
        int j1 = Mth.floor(pArea.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for(int k1 = i; k1 <= l; ++k1) {
            for(int l1 = j; l1 <= i1; ++l1) {
                for(int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    if (!blockstate.isAir() && !blockstate.is(BlockTags.DRAGON_TRANSPARENT)) {
                        if (net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.level(), blockpos, this)) {
                            flag1 = this.level().destroyBlock(blockpos, true) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPos blockpos1 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(i1 - j + 1), k + this.random.nextInt(j1 - k + 1));
            this.level().levelEvent(2008, blockpos1, 0);
        }

        return flag;
    }
    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }
    public boolean isClimbing() {
        if (getTarget() != null) {
            return entityData.get(CLIMBING_ACCESSOR);
        }
        return false;
    }
    public void setClimbing(boolean pClimbing) {
        this.entityData.set(CLIMBING_ACCESSOR, pClimbing);
    }


    public static int getGracePeriod(){
        return (int) (RevervoxModServerConfigs.REVERVOX_AFTER_SPEAK_GRACE_PERIOD.get()*1000);
    }


    public static boolean checkRevervoxSpawnRules(EntityType<RevervoxGeoEntity> pRevervox, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getNearestEntity(RevervoxGeoEntity.class,
                TargetingConditions.DEFAULT,
                null,
                pPos.getX(),
                pPos.getY(), pPos.getZ(),
                new AABB(pPos).inflate(100)) != null) {

            return false;
        }
        if (pLevel.getMaxLocalRawBrightness(pPos) < 4) {
            // Priority to spawn on alone player
            Player player = pLevel.getNearestPlayer(TargetingConditions.DEFAULT, pPos.getX(), pPos.getY(), pPos.getZ());
            if (player != null){
                if (player.position().y <= pLevel.getSeaLevel()){
                    if (player.level().getNearbyPlayers(TargetingConditions.DEFAULT, player, player.getBoundingBox().inflate(100, 50, 100)).isEmpty()){
                        boolean flag = checkMobSpawnRules(pRevervox, pLevel, pSpawnType, pPos, pRandom);
                        if (flag) {
                            RevervoxMod.LOGGER.info("Trying to Spawn Revervox on alone player: " + player.getName() + " at " + pPos);
                        }
                        return flag;
                    }
                }
            }
        }
        if (pPos.getY() >= pLevel.getSeaLevel()) {
            return false;
        } else {
            // Check if there are other Revervox around
            int i = pLevel.getMaxLocalRawBrightness(pPos);
            int j = 4;
            if (pRandom.nextBoolean()) {
                return false;
            }
            boolean flag1 = i <= pRandom.nextInt(j) && checkMobSpawnRules(pRevervox, pLevel, pSpawnType, pPos, pRandom);
            if (flag1) {
                RevervoxMod.LOGGER.info("Trying to Spawn Revervox at " + pPos);
            }
            return flag1;

        }
    }
    public void addParticlesAroundSelf(ParticleOptions pParticleOption) {
        addParticlesAroundSelf(pParticleOption, 1.0);
    }

    public void addParticlesAroundSelf(ParticleOptions pParticleOption, double radius) {
        addParticlesAroundSelf(pParticleOption, radius, 30);
    }

    public void addParticlesAroundSelf(ParticleOptions pParticleOption, double radius, int particleCount) {
        for(int i = 0; i < particleCount; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 2.0 * radius;
            double offsetY = (this.random.nextDouble() - 0.5) * 2.0 * radius;
            double offsetZ = (this.random.nextDouble() - 0.5) * 2.0 * radius;

            double particleX = this.getX() + offsetX;
            double particleY = this.getY() + 1.0 + offsetY;
            double particleZ = this.getZ() + offsetZ;

            double velocityScale = radius * 0.1;
            double velX = (this.random.nextDouble() - 0.5) * velocityScale;
            double velY = (this.random.nextDouble() - 0.5) * velocityScale;
            double velZ = (this.random.nextDouble() - 0.5) * velocityScale;

            this.level().addParticle(pParticleOption, particleX, particleY, particleZ, velX, velY, velZ);
        }
    }
}
