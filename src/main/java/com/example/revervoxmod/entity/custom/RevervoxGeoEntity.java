package com.example.revervoxmod.entity.custom;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.voicechat.RecordedPlayer;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.entity.goals.RandomRepeatGoal;
import com.example.revervoxmod.entity.goals.TargetSpokeGoal;
import com.example.revervoxmod.registries.SoundRegistry;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
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
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

// TODO - ATTACK ANIMATION, JUMP ANIMATION, JUMP ATTACK
public class RevervoxGeoEntity extends Monster implements GeoEntity, NeutralMob {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.revervox.idle");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.revervox.chase");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(7, 12);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public RevervoxGeoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        //controllers.add(new AnimationController<>(this, "attackController", 5, this::attackPredicate));
    }

    /*
    private PlayState attackPredicate(AnimationState event) {
        if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().forceAnimationReset(); // substitui markneedsreload????
            event.getController().setAnimation(ATTACK);
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

     */

    protected <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (event.isMoving()){
            event.getController().setAnimation(RUN);
        return PlayState.CONTINUE;
        }

        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    @Override
    protected void registerGoals() {
        // So it doesn't sink in the water
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(2, new RandomRepeatGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, Ingredient.of(Items.MUSIC_DISC_13), false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.3F));
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
        return SoundRegistry.JUMPSCARE.get();
    }

    public boolean isSpeakingAtMe(Player player) {
        if (RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()) != null){
            return RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID()).isSpeaking();
        } else return false;
    }

    public void disappear(Player player, VoicechatServerApi api){
        Vec3 loc = this.getEyePosition();
        AudioChannel channel = api.createLocationalAudioChannel(UUID.randomUUID(), api.fromServerLevel(player.getCommandSenderWorld()), api.createPosition(loc.x, loc.y, loc.z));
        if(channel == null){
            RevervoxMod.LOGGER.error("Couldn't create disappearing channel");
            return;
        }
        channel.setCategory(RevervoxVoicechatPlugin.REVERVOX_CATEGORY);
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
        RevervoxMod.LOGGER.info("Playing kill audio: {}", audio);
        AudioPlayer audioPlayer = new AudioPlayer(audio, api, channel);
        audioPlayer.start();
        this.remove(RemovalReason.DISCARDED);
    }
    public void addParticlesAroundSelf(ParticleOptions pParticleOption) {
        for(int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(pParticleOption, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }
    }
}
