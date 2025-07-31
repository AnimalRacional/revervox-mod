package com.example.revervoxmod.entity.goals;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Set;
import java.util.UUID;

public class RandomRepeatGoal extends Goal {
    private final Mob mob;
    private static final int CHANNEL_DISTANCE = 20;
    private EntityAudioChannel channel;
    public RandomRepeatGoal(RevervoxGeoEntity revervoxGeoEntity) {
        this.mob = revervoxGeoEntity;
    }
    @Override
    public boolean canUse() {
        return this.mob.getRandom().nextFloat() < 0.01F;
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
        if (RevervoxMod.vcApi instanceof VoicechatServerApi api){
            Set<UUID> keyset = RevervoxVoicechatPlugin.getRecordedPlayers().keySet();
            if (keyset.isEmpty()) return;
            UUID randomUUID = keyset.stream().skip(mob.getRandom().nextInt(keyset.size())).findFirst().orElse(null);

            new AudioPlayer(RevervoxVoicechatPlugin.getRecordedPlayer(randomUUID).getRandomAudio(), api, channel).start();
        }
    }

    private static EntityAudioChannel createChannel(VoicechatServerApi api, UUID channelID, String category, Entity nearestEntity) {
        EntityAudioChannel channel = api.createEntityAudioChannel(channelID, api.fromEntity(nearestEntity));
        if (channel == null) {
            RevervoxMod.LOGGER.error("Couldn't create channel");
            return null;
        }
        channel.setCategory(category); // The category of the audio channel
        channel.setDistance(RandomRepeatGoal.CHANNEL_DISTANCE); // The distance in which the audio channel can be heard
        return channel;
    }


}
