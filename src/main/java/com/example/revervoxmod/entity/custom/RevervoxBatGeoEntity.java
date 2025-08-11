package com.example.revervoxmod.entity.custom;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.goals.TargetSpokeGoal;
import com.example.revervoxmod.registries.ParticleRegistry;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.voicechat.audio.AudioEffect;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class RevervoxBatGeoEntity extends Bat implements GeoEntity, NeutralMob, HearingEntity, SpeakingEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private AudioPlayer currentAudioPlayer;
    @Nullable
    private UUID persistentAngerTarget;
    public RevervoxBatGeoEntity(EntityType<? extends Bat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new TargetSpokeGoal<>(this, this::isAngryAt));
        super.registerGoals();
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
        controllers.add(DefaultAnimations.genericFlyIdleController(this).transitionLength(5),
                DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(5));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public void onKilledPlayer(Player player, VoicechatServerApi api){
        playPlayerAudio(player, api, createLocationalAudioChannel(api), new AudioEffect().setPitchEnabled(1.7f));
        this.remove(Entity.RemovalReason.DISCARDED);
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
    public void remove(RemovalReason pReason) {
        VoicechatServerApi api = (VoicechatServerApi) RevervoxMod.vcApi;
        short[] audio = RevervoxVoicechatPlugin.getRandomAudio(true);
        if (audio != null) {
            playAudio(audio, api, createLocationalAudioChannel(api), new AudioEffect().setPitchEnabled(1.5f));
        }
        super.remove(pReason);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.addParticlesAroundSelf(ParticleRegistry.REVERVOX_PARTICLES.get(), 1);
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

    @Override
    public AudioPlayer getCurrentAudioPlayer() {
        return this.currentAudioPlayer;
    }

    @Override
    public void setCurrentAudioPlayer(AudioPlayer player) {
        this.currentAudioPlayer = player;
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

    @Override
    public void onSpeak(long audioDuration) {
    }
}
