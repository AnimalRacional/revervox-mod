package com.example.revervoxmod.entity.custom;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.goals.RandomRepeatGoal;
import com.example.revervoxmod.entity.goals.TargetSpokeGoal;
import com.example.revervoxmod.registries.ParticleRegistry;
import com.example.revervoxmod.registries.SoundRegistry;
import com.example.revervoxmod.voicechat.RecordedPlayer;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class RevervoxGeoEntity extends Monster implements GeoEntity, NeutralMob {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(7, 12);
    private int remainingPersistentAngerTime;
    private AudioPlayer currentAudioPlayer;
    private boolean canBeAngry = false;
    @Nullable
    private UUID persistentAngerTarget;

    public RevervoxGeoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        // So it doesn't sink in the water
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(3, new RandomRepeatGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(Items.MUSIC_DISC_13), false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.addBehaviourGoals();

    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.7D, false));
        this.targetSelector.addGoal(1, new TargetSpokeGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 7D);
    }

    @Override
    public int getCurrentSwingDuration() {
        return 5 + 5 + 1;
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
        RevervoxMod.LOGGER.info("Starting persistent anger timer!");
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.THINGY_HURT.get();
    }

    public boolean isSpeakingAtMe(Player player) {
        if (RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()) != null){
            return RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()).isSpeaking();
        } else return false;
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
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
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

    public void playAudio(Player player, VoicechatServerApi api){
        Vec3 loc = this.getEyePosition();
        AudioChannel channel = api.createLocationalAudioChannel(UUID.randomUUID(), api.fromServerLevel(player.getCommandSenderWorld()), api.createPosition(loc.x, loc.y, loc.z));
        if(channel == null){
            RevervoxMod.LOGGER.error("Couldn't create disappearing channel");
            return;
        }
        channel.setCategory(RevervoxVoicechatPlugin.REVERVOX_CATEGORY);
        playAudio(player, api, channel);
    }

    public void playAudio(Player player, VoicechatServerApi api, AudioChannel channel){
        // TODO tanto aqui como no RandomRepeatGoal, é possível o áudio aleatório ser null mesmo quando há audios pois só verificamos o player do evento e um outro player aleatório, que podem ambos ter 0 audios mesmo outros players tendo audios
        RecordedPlayer record = RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID());
        Path audio = record.getRandomAudio();
        if(audio == null){
            RevervoxMod.LOGGER.error("No audio found for {}, choosing random player", player.getName());
            Set<UUID> keyset = RevervoxVoicechatPlugin.getRecordedPlayers().keySet();
            if (keyset.isEmpty()) return;
            UUID randomUUID = keyset.stream().skip(new Random().nextInt(keyset.size())).findFirst().orElse(null);

            audio = RevervoxVoicechatPlugin.getRecordedPlayer(randomUUID).getRandomAudio();
            if (audio == null) return;
        }
        RevervoxMod.LOGGER.info("Playing audio: {}", audio);
        currentAudioPlayer = new AudioPlayer(audio, api, channel);
        currentAudioPlayer.start();
    }

    public void disappear(Player player, VoicechatServerApi api){
        playAudio(player, api);
        this.remove(RemovalReason.DISCARDED);
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


    public AudioPlayer getCurrentAudioPlayer() {
        return currentAudioPlayer;
    }

    public boolean isCanBeAngry() {
        return canBeAngry;
    }

    public void setCanBeAngry(boolean canBeAngry) {
        this.canBeAngry = canBeAngry;
    }
}
