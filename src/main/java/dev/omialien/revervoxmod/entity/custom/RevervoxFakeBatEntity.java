package dev.omialien.revervoxmod.entity.custom;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.networking.packets.AddSoundInstancePacket;
import dev.omialien.revervoxmod.particle.ParticleManager;
import dev.omialien.revervoxmod.registries.ParticleRegistry;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import dev.omialien.voicechat_recording.RecordingSimpleVoiceChat;
import dev.omialien.voicechat_recording.voicechat.RevervoxVoicechatPlugin;
import dev.omialien.voicechat_recording.voicechat.audio.AudioEffect;
import dev.omialien.voicechat_recording.voicechat.audio.AudioPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class RevervoxFakeBatEntity extends FlyingMob implements GeoEntity, IRevervoxEntity, SpeakingEntity {
    private final AnimatableInstanceCache geoCache;
    private final int TICKS_TO_UPDATE_ROTATION = 200;
    private int ticksLeft = 3;
    private final float MOVE_SPEED = 0.6f;
    private Vec3 movement = new Vec3(0,0,MOVE_SPEED);
    private Player target;
    private int ticksToDie = 20;
    private boolean hasPlayedSpawnSound = false;

    public RevervoxFakeBatEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        geoCache = GeckoLibUtil.createInstanceCache(this);
        this.noPhysics = true;
    }

    public void setTarget(Player player){
        this.target = player;
    }

    @Override
    public void push(@NotNull Entity pEntity) {    }

    @Override
    protected void pushEntities() {    }

    @Override
    protected void doPush(@NotNull Entity p_20971_) {    }

    @Override
    public boolean isPushable() {return false; }

    @Override
    public void push(double pX, double pY, double pZ) {    }

    @Override
    public void tick() {
        if (target != null && target.isAlive()){
            if(!this.level().isClientSide() && !hasPlayedSpawnSound){
                RevervoxPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) target), new AddSoundInstancePacket(this.getId(), SoundRegistry.REVERVOX_BAT_ALERT.get(), SoundSource.HOSTILE, false));
                hasPlayedSpawnSound = true;
            }
        }
        super.tick();
    }

    @Override
    protected void customServerAiStep() {
        ticksLeft--;
        if(ticksLeft <= 0){
            ticksLeft = TICKS_TO_UPDATE_ROTATION;
            setRotation(getYRot());
        }
        setDeltaMovement(movement);
        ticksToDie--;
        if(ticksToDie <= 0){
            if (target != null && target.isAlive()){
                target.hurt(this.level().damageSources().mobAttack(this), 1);
            }
            this.remove(RemovalReason.KILLED);
        }
        super.customServerAiStep();
    }

    @Override
    public void onRemovedFromWorld() {
        ParticleManager.addParticlesAroundSelf(ParticleRegistry.REVERVOX_PARTICLES.get(), this);
        super.onRemovedFromWorld();
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if(!this.level().isClientSide() && pReason == RemovalReason.KILLED && RecordingSimpleVoiceChat.vcApi instanceof VoicechatServerApi api){
            AudioChannel channel = api.createLocationalAudioChannel(UUID.randomUUID(), api.fromServerLevel(this.level()), api.createPosition(this.getX(), this.getY(), this.getZ()));
            if(channel != null){
                channel.setCategory(RevervoxVoicechatPlugin.REVERVOX_CATEGORY);
                short[] audio = RevervoxVoicechatPlugin.getRandomAudio(false);
                if(audio != null){
                    this.playAudio(audio, api, channel, new AudioEffect().changePitch(1.5f).makeReverb(0.5f, 160, 2));
                }
            }
        }
        super.remove(pReason);
    }

    public void setRotation(float degrees){
        this.setYRot(degrees);
        float yaw = (float) (degrees * (Math.PI/180));
        float sz = Mth.cos(yaw)*MOVE_SPEED;
        float sx = Mth.sin(yaw)*MOVE_SPEED;
        this.movement = new Vec3(-sx, 0, sz);
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<RevervoxFakeBatEntity> controller = new AnimationController<>(this, "Attack", 5, state -> {
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
    public boolean doHurtTarget(@NotNull Entity target) {
        boolean result = super.doHurtTarget(target);
        // Trigger the GeckoLib attack animation
        triggerAnim("Attack", "attack.bite");
        this.remove(RemovalReason.DISCARDED);
        return result;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;

    }

    // TODO n funfa
    @Override
    public boolean isInvisibleTo(@NotNull Player pPlayer) {
        if(target == null){ return false; }
        return pPlayer != target;

    }



    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FLYING_SPEED, 3.5D)
                .add(Attributes.ATTACK_SPEED, 1.8D);
    }
    private AudioPlayer currentAudioPlayer;
    @Override
    public AudioPlayer getCurrentAudioPlayer() {
        return currentAudioPlayer;
    }

    @Override
    public void setCurrentAudioPlayer(AudioPlayer player) {
        this.currentAudioPlayer = player;
    }

    @Override
    public void onSpeak(long audioDuration) {

    }
}
