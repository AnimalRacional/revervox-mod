package com.example.revervoxmod.entity.goals;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.nio.file.Path;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class RandomRepeatGoal extends Goal {
    private final Mob mob;
    private static final int CHANNEL_DISTANCE = 20;
    private EntityAudioChannel channel;
    private AudioPlayer currentAudioPlayer;
    public RandomRepeatGoal(RevervoxGeoEntity revervoxGeoEntity) {
        this.mob = revervoxGeoEntity;
    }
    @Override
    public boolean canUse() {
        return new Random().nextFloat() < 0.006F && mob.getTarget() == null;
    }

    public boolean canContinueToUse() {
        return false;
    }

    public void start() {
        if (RevervoxMod.vcApi instanceof VoicechatServerApi api){
            UUID channelID = UUID.randomUUID();
            channel = createChannel(api, channelID, RevervoxVoicechatPlugin.REVERVOX_CATEGORY, this.mob);
        }
    }

    public void tick() {
        if (currentAudioPlayer != null && currentAudioPlayer.isPlaying()) return;
        if (RevervoxMod.vcApi instanceof VoicechatServerApi api){
            Set<UUID> keyset = RevervoxVoicechatPlugin.getRecordedPlayers().keySet();
            if (keyset.isEmpty()) return;
            UUID randomUUID = keyset.stream().skip(new Random().nextInt(keyset.size())).findFirst().orElse(null);

            Path randomAudio = RevervoxVoicechatPlugin.getRecordedPlayer(randomUUID).getRandomAudio();
            if (randomAudio == null) return;
            currentAudioPlayer = new AudioPlayer(randomAudio, api, channel);
            currentAudioPlayer.start();
        }
    }

    private static EntityAudioChannel createChannel(VoicechatServerApi api, UUID channelID, String category, Entity nearestEntity) {
        EntityAudioChannel channel = api.createEntityAudioChannel(channelID, api.fromEntity(nearestEntity));
        if (channel == null) {
            RevervoxMod.LOGGER.error("Couldn't create channel");
            return null;
        }
        channel.setCategory(category);
        channel.setDistance(RandomRepeatGoal.CHANNEL_DISTANCE);
        return channel;
    }


}
