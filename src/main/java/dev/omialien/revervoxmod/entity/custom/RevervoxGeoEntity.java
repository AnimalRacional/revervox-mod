package dev.omialien.revervoxmod.entity.custom;

import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.ai.MMEntityMoveHelper;
import dev.omialien.revervoxmod.entity.ai.RVClimbNavigation;
import dev.omialien.revervoxmod.entity.goals.*;
import dev.omialien.revervoxmod.particle.ParticleManager;
import dev.omialien.revervoxmod.registries.DamageTypeRegistry;
import dev.omialien.revervoxmod.registries.ParticleRegistry;
import dev.omialien.revervoxmod.registries.RevervoxTags;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import dev.omialien.voicechatrecording.VoiceChatRecording;
import dev.omialien.voicechatrecording.voicechat.audio.AudioPlayer;
import dev.omialien.voicechatrecording.api.AudioEffect;
import dev.omialien.voicechatrecording.api.IRecordedPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.IFluidBlock;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class RevervoxGeoEntity extends Monster implements GeoEntity, NeutralMob, HearingEntity, SpeakingEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Boolean> CLIMBING_ACCESSOR = SynchedEntityData.defineId(RevervoxGeoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(50, 60);
    private final RawAnimation REVERVO_CLIMB = RawAnimation.begin().thenLoop("move.climb");
    private int remainingPersistentAngerTime;
    private long firstSpeak;
    private boolean shouldDisappear;
    private static final long NOT_SPOKEN_YET = -1;
    private AudioPlayer currentAudioPlayer;
    private long noLineOfSightTicks;
    private boolean stunned;
    @Nullable
    private UUID persistentAngerTarget;
    private int breakCooldown;

    public RevervoxGeoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        moveControl = new MMEntityMoveHelper(this, 90);
        firstSpeak = NOT_SPOKEN_YET;
        breakCooldown = 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CLIMBING_ACCESSOR, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkRunIdleController(this).transitionLength(5)
                        .triggerableAnim("Stun", RawAnimation.begin().then("misc.stun", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("SonicBoom", DefaultAnimations.ATTACK_CAST),
                DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_SWING).transitionLength(5),
                new AnimationController<GeoAnimatable>(this, "Climb", 5, state ->{
                    if (this.isClimbing()){
                        return state.setAndContinue(REVERVO_CLIMB);
                    }

                    state.resetCurrentAnimation();

                    return PlayState.STOP;
                }));
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        ParticleManager.addParticlesAroundSelf(ParticleRegistry.REVERVOX_PARTICLES.get(), 2, this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    @Override
    protected void registerGoals() {
        RevervoxMod.LOGGER.debug("Revervox Spawned");
        // So it doesn't sink in the water
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new RandomRepeatGoal(this));
        this.goalSelector.addGoal(5, new TemptGoal(this, 0.4D, Ingredient.of(Items.SPIDER_EYE), false));
        this.goalSelector.addGoal(6, new TemptGoal(this, 0.4D, Ingredient.of(Items.FERMENTED_SPIDER_EYE), false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.addBehaviourGoals();
    }


    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(0, new RevervoxStunGoal(this));
        this.goalSelector.addGoal(1, new RevervoxSonicBoomGoal(this));
        this.goalSelector.addGoal(2, new EatFoodGoal(this, RevervoxTags.Items.ATTRACTS_REVERVOX));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.7D, false));
        this.targetSelector.addGoal(1, new TargetSpokeGoal<>(this, this::isAngryAt, SoundRegistry.REVERVOX_ALERT.get(), SoundRegistry.REVERVOX_LOOP.get(), 50));
        this.targetSelector.addGoal(2, new RevervoxHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, (entity) -> entity.getType().is(RevervoxTags.Entities.INSECTS)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 7D)
                .add(Attributes.ATTACK_SPEED, 0.3D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean canDisableShield() {
        return true;
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
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return pDistanceToClosestPlayer > 200.0D;
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
    public boolean hurt(DamageSource pSource, float pAmount) {
        float amount = pAmount;
        if(pSource.type() != damageSources().source(DamageTypeRegistry.REVERVOX_BONUS).type()){
            amount /= 2;
        }
        return super.hurt(pSource, amount);
    }

    @Override
    public @NotNull ItemStack eat(Level pLevel, @NotNull ItemStack pFood) {
        pLevel.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(pFood), SoundSource.NEUTRAL, 1.0F, 1.0F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.4F);

        this.gameEvent(GameEvent.EAT);
        return pFood;
    }

    @Override
    public void awardKillScore(@NotNull Entity pEntity, int pScoreValue, @NotNull DamageSource pSource) {
        if(pEntity instanceof Player player){
            Vec3 loc = this.getEyePosition();
            playPlayerAudio(player, VoiceChatRecording.vcApi, () -> {
                AudioChannel channel = VoiceChatRecording.vcApi.createLocationalAudioChannel(UUID.randomUUID(), VoiceChatRecording.vcApi.fromServerLevel(this.level()), VoiceChatRecording.vcApi.createPosition(loc.x, loc.y, loc.z));
                if(channel == null){
                    RevervoxMod.LOGGER.error("Couldn't create disappearing channel");
                    return null;
                }
                channel.setCategory(RevervoxMod.MOD_ID);
                return channel;
            }, new AudioEffect().addRandomEffects());
            this.remove(Entity.RemovalReason.DISCARDED);
        }
        super.awardKillScore(pEntity, pScoreValue, pSource);
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

    //TODO fazer depender da distancia que o player ta dele
    @Override
    public boolean isSpeakingAtMe(Player player) {
        long time = System.currentTimeMillis();
        if(hasSpoken() && time >= getGracePeriodEnd()){
            IRecordedPlayer rec = RevervoxMod.RECORDING_API.getRecordedPlayer(player.getUUID());
            if (rec != null){
                return rec.isSpeaking() &&
                        rec.getLastSpoke() >= getGracePeriodEnd();
            } else return false;
        }
        return false;
    }

    @Override
    public void setTarget(@org.jetbrains.annotations.Nullable LivingEntity target) {
        if (target instanceof Player){
            this.setShouldDisappear(true);
        }
        super.setTarget(target);
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
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, pX, pY, pZ);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
            }

            return flag2;
        } else {
            return false;
        }
    }

    @Override
    public float getStepHeight() {
        return 1;
    }

    private boolean isFluid(Block block){
        return block instanceof LiquidBlock || block instanceof IFluidBlock;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return isFluid(level().getBlockState(blockPosition().above()).getBlock())
                || isFluid(level().getBlockState(blockPosition().above().above()).getBlock());
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            Vec3i offset = this.getDirection().getNormal();
            boolean isFacingSolid = !this.level().getBlockState(blockPosition().relative(getDirection())).isAir();

            if (isFacingSolid) {
                offset = offset.offset(0, 1, 0);
            }

            if (this.getTarget() != null) {
                if (!this.hasLineOfSight(this.getTarget())) {
                    this.noLineOfSightTicks++;
                } else {
                    resetLineOfSight();
                }
            }

            boolean isFacingBelowSolid = !this.level().getBlockState(blockPosition().relative(getDirection()).below()).isAir();
            boolean isOffsetFacingTwoAboveSolid = !this.level().getBlockState(blockPosition().offset(offset).above(2)).isAir();

            if (this.isInFluidType()){
                this.setDeltaMovement(this.getDeltaMovement().scale(1.0D));
            }

            this.setClimbing((this.horizontalCollision && this.getTarget() != null) && (isOffsetFacingTwoAboveSolid || !isFacingBelowSolid) && (breakCooldown <= 0) && (this.getTarget().getY() > this.getY()));
            this.setSprinting(this.getTarget() != null);
        }
    }

    public boolean lostLineOfSightFor(long pTicks) {
        return this.noLineOfSightTicks >= pTicks;
    }
    public void resetLineOfSight(){
        this.noLineOfSightTicks = 0;
    }

    public void resetNavigation(){
        if (this.getTarget() == null) return;
        this.getNavigation().moveTo(this.getTarget(), this.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }

    @Override
    public void updatePersistentAnger(@NotNull ServerLevel pServerLevel, boolean pUpdateAnger) {
        if (this.getTarget() == null || !this.hasLineOfSight(this.getTarget())) {
            NeutralMob.super.updatePersistentAnger(pServerLevel, pUpdateAnger);
        }
    }

    @Override
    protected void customServerAiStep() {
        boolean targetDirectlyAboveThreeBlocks = this.getTarget() != null && this.getTarget().getY() - this.getY() >= 3.0D;
        if (this.getTarget() != null) {
            double playerDirectionOffset = (this.getTarget().getY() - this.getY());
            double offset = Double.compare(playerDirectionOffset, 0.0D);
            offset = offset < 0.0D ? -1.0D : offset == 0 ? 0.0D : 1.0D;
            if(
                    (RevervoxModServerConfigs.REVERVOX_BREAKS_BLOCKS.get() ||
                            RevervoxModServerConfigs.REVERVOX_BREAKS_NONSOLID.get())
            ){
                if(breakCooldown > 0){ breakCooldown--; }
                this.checkWalls(this.getBoundingBox().inflate(0.4D, 0, 0.2D).move(0, offset, 0),
                        RevervoxModServerConfigs.REVERVOX_BREAKS_BLOCKS.get()
                                && (breakCooldown <= 0)
                                && !this.hasLineOfSight(this.getTarget()) && !targetDirectlyAboveThreeBlocks
                );

            }
        } else {
            if (shouldDisappear()) {
                this.remove(RemovalReason.DISCARDED);
            }
        }

        super.customServerAiStep();
    }

    private void checkWalls(AABB pArea, boolean breakSolid) {
        int i = Mth.floor(pArea.minX);
        int j = Mth.floor(pArea.minY);
        int k = Mth.floor(pArea.minZ);
        int l = Mth.floor(pArea.maxX);
        int i1 = Mth.floor(pArea.maxY);
        int j1 = Mth.floor(pArea.maxZ);

        for(int k1 = i; k1 <= l; ++k1) {
            for(int l1 = j; l1 <= i1; ++l1) {
                if(getTarget() != null && l1 < getTarget().getY() && l1 <= this.getY()){
                    continue;
                }
                for(int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    if (!blockstate.isAir() && !blockstate.is(BlockTags.DRAGON_TRANSPARENT) && (!blockstate.is(BlockTags.DRAGON_IMMUNE) || blockstate.is(Blocks.IRON_BARS))) {
                        if (net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.level(), blockpos, this)) {
                            if (breakSolid && this.getY() <= level().getSeaLevel()) {
                                if(this.getY() < 0 || blockstate.is(BlockTags.ANCIENT_CITY_REPLACEABLE)){
                                    this.level().destroyBlock(blockpos, true, this);
                                } else {
                                    RevervoxMod.TASKS.schedule(() -> {
                                        if(this.isAlive() && !this.isRemoved()){
                                            this.level().destroyBlock(blockpos, true, this);
                                        }
                                    }, 5);
                                    breakCooldown = 6;
                                }
                            } else if(RevervoxModServerConfigs.REVERVOX_BREAKS_NONSOLID.get() && (!blockstate.isSolid() || blockstate.is(BlockTags.LEAVES))){
                                this.level().destroyBlock(blockpos, true, this);
                            }
                        }
                    }
                }
            }
        }
    }
    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }
    public boolean isClimbing() {
        return entityData.get(CLIMBING_ACCESSOR);
    }
    public void setClimbing(boolean pClimbing) {
        this.entityData.set(CLIMBING_ACCESSOR, pClimbing);
    }

    public boolean isStunned(){
        return this.stunned;
    }

    public void setStunned(boolean pStunned){
        this.stunned = pStunned;
    }

    public long getGracePeriodEnd(){
        long grace = (long)(RevervoxModServerConfigs.REVERVOX_AFTER_SPEAK_GRACE_PERIOD.get()*1000);
        return (getFirstSpoken() + grace);
    }


    public static boolean checkRevervoxSpawnRules(EntityType<RevervoxGeoEntity> pRevervox, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        // Check if there are other Revervox around
        if (pLevel.getNearestEntity(RevervoxGeoEntity.class,
                TargetingConditions.DEFAULT,
                null,
                pPos.getX(),
                pPos.getY(), pPos.getZ(),
                new AABB(pPos).inflate(RevervoxModServerConfigs.REVERVOX_SPAWN_CHANCE.get())) != null) {

            return false;
        }
        if (pLevel.getMaxLocalRawBrightness(pPos) < 4) {
            // Priority to spawn on alone player
            Player player = pLevel.getNearestPlayer(TargetingConditions.DEFAULT, pPos.getX(), pPos.getY(), pPos.getZ());
            if (player != null) {
                if (!RevervoxModServerConfigs.REVERVOX_ABOVE_GROUND.get() && player.position().y <= pLevel.getSeaLevel() - 10){
                    if (player.level().getNearbyPlayers(TargetingConditions.DEFAULT, player, player.getBoundingBox().inflate(100, 50, 100)).isEmpty()){
                        boolean flag = checkMobSpawnRules(pRevervox, pLevel, pSpawnType, pPos, pRandom);
                        if (flag) {
                            RevervoxMod.LOGGER.debug("Trying to Spawn Revervox on alone player: " + player.getName() + " at " + pPos);
                        }
                        return flag;
                    }
                }
            }
        }
        if (!RevervoxModServerConfigs.REVERVOX_ABOVE_GROUND.get() && pPos.getY() >= pLevel.getSeaLevel() - 10) {
            return false;
        } else {

            int i = pLevel.getMaxLocalRawBrightness(pPos);
            int j = 4;
            if (pRandom.nextBoolean()) {
                return false;
            }
            boolean flag1 = i <= pRandom.nextInt(j) && checkMobSpawnRules(pRevervox, pLevel, pSpawnType, pPos, pRandom);
            if (flag1) {
                RevervoxMod.LOGGER.debug("Trying to Spawn Revervox at " + pPos);
            }
            return flag1;

        }
    }

    public boolean shouldDisappear() {
        return shouldDisappear;
    }

    private void setShouldDisappear(boolean shouldDisappear) {
        this.shouldDisappear = shouldDisappear;
    }
}
