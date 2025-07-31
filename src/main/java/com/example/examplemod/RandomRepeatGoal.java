package com.example.examplemod;

import com.example.examplemod.entity.custom.RevervoxGeoEntity;
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
        if (ExampleMod.vcApi instanceof VoicechatServerApi api){
            UUID channelID = UUID.randomUUID();
            channel = createChannel(api, channelID, ExampleVoicechatPlugin.REVERVOX_CATEGORY, this.mob);
        }
    }

    public void tick() {
        if (ExampleMod.vcApi instanceof VoicechatServerApi api){
            Set<UUID> keyset = ExampleVoicechatPlugin.getRecordedPlayers().keySet();
            if (keyset.size() <= 0) return;
            UUID randomUUID = keyset.stream().skip(mob.getRandom().nextInt(keyset.size())).findFirst().orElse(null);

            new AudioPlayer(ExampleVoicechatPlugin.getRecordedPlayer(randomUUID).getRandomAudio(), api, channel).start();
        }
    }

    private static EntityAudioChannel createChannel(VoicechatServerApi api, UUID channelID, String category, Entity nearestEntity) {
        EntityAudioChannel channel = api.createEntityAudioChannel(channelID, api.fromEntity(nearestEntity));
        if (channel == null) {
            ExampleMod.LOGGER.error("Couldn't create channel");
            return null;
        }
        channel.setCategory(category); // The category of the audio channel
        channel.setDistance(RandomRepeatGoal.CHANNEL_DISTANCE); // The distance in which the audio channel can be heard
        return channel;
    }


}
