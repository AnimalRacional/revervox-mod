package dev.omialien.revervoxmod.items;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.voicechat_recording.RecordingSimpleVoiceChat;
import dev.omialien.voicechat_recording.voicechat.RecordingSimpleVoiceChatPlugin;
import dev.omialien.voicechat_recording.voicechat.audio.AudioPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ThrowableAudio extends ThrowableItemProjectile {
    private boolean hasPlayed = false;
    private final int CHANNEL_DISTANCE = 50;

    public ThrowableAudio(EntityType<? extends Snowball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrowableAudio(Level pLevel, LivingEntity pShooter) {
        super(EntityType.SNOWBALL, pShooter, pLevel);
    }

    public ThrowableAudio(Level pLevel, double pX, double pY, double pZ) {
        super(EntityType.SNOWBALL, pX, pY, pZ, pLevel);
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItemRaw();
        return (itemstack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemstack));
    }
    protected @NotNull Item getDefaultItem() {
        return Items.SNOWBALL;
    }
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        int i = entity instanceof RevervoxGeoEntity ? 3 : 0;
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float)i);
        if (entity instanceof Player player && RecordingSimpleVoiceChat.vcApi instanceof VoicechatServerApi api) {
            short[] audio = RecordingSimpleVoiceChatPlugin.getRandomAudio(player.getUUID(), false);
            if(audio != null){
                playAudio(player.position(), api, audio);
            } else {
                RevervoxMod.LOGGER.debug("No audio to play for throwable");
            }
        }
    }

    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);
        Vec3 hitLocation = pResult.getLocation();
        if (!this.level().isClientSide && !pResult.getType().equals(HitResult.Type.ENTITY) && RecordingSimpleVoiceChat.vcApi instanceof VoicechatServerApi api) {
            if (!hasPlayed) {
                short[] audio = RecordingSimpleVoiceChatPlugin.getRandomAudio(false);
                if(audio != null){
                    playAudio(hitLocation, api, audio);

                    AABB aabb = new AABB(hitLocation.x - CHANNEL_DISTANCE, hitLocation.y - CHANNEL_DISTANCE, hitLocation.z - CHANNEL_DISTANCE, hitLocation.x + CHANNEL_DISTANCE, hitLocation.y + CHANNEL_DISTANCE, hitLocation.z + CHANNEL_DISTANCE);
                    this.level().getNearbyEntities(RevervoxGeoEntity.class, TargetingConditions.DEFAULT, null, aabb).forEach(entity -> {
                        entity.setAngerLocation(hitLocation);
                    });

                } else {
                    RevervoxMod.LOGGER.debug("No audio to play for throwable");
                }
            }
        }

    }

    private void playAudio(Vec3 loc, VoicechatServerApi api, short[] audio){
        if(audio != null){
            LocationalAudioChannel channel = api.createLocationalAudioChannel(UUID.randomUUID(), api.fromServerLevel(this.level()), api.createPosition(loc.x, loc.y, loc.z));
            if(channel != null) {
                channel.setCategory(RevervoxMod.MOD_ID);
                channel.setDistance(CHANNEL_DISTANCE);
                new AudioPlayer(audio, api, channel).start();
                hasPlayed = true;

            } else {
                RevervoxMod.LOGGER.debug("Channel is null! (ThrowableAudio)");
            }
        }
    }
}
