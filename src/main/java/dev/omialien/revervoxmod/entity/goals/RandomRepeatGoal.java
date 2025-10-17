package dev.omialien.revervoxmod.entity.goals;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.voicechatrecording.VoiceChatRecording;
import dev.omialien.voicechatrecording_api.AudioEffect;
import dev.omialien.voicechatrecording_api.IRecordedAudio;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.stream.Collectors;

public class RandomRepeatGoal extends Goal {
    private final RevervoxGeoEntity mob;
    private static final int CHANNEL_DISTANCE = 50;
    private EntityAudioChannel channel;
    private int audiosPlayed = 0;
    private boolean canSpeak = true;
    public RandomRepeatGoal(RevervoxGeoEntity revervoxGeoEntity) {
        this.mob = revervoxGeoEntity;
        RevervoxMod.TASKS.schedule(setCanSpeak(), 0);
    }
    @Override
    public boolean canUse() {
        return canSpeak && mob.getTarget() == null;
    }

    private AudioChannel getChannel(){
        if (channel == null){
            UUID channelID = UUID.randomUUID();
            channel = createChannel(VoiceChatRecording.vcApi, channelID, this.mob);
        }
        return channel;
    }

    @Override
    public void stop() {
        channel = null;
        super.stop();
    }

    public boolean canContinueToUse() {
        return false;
    }

    public void tick() {
        canSpeak = false;
        if (audiosPlayed >= RevervoxModServerConfigs.REVERVOX_MAX_AUDIOS.get()) this.mob.remove(Entity.RemovalReason.DISCARDED);
        RevervoxMod.LOGGER.debug("Less than " + RevervoxModServerConfigs.REVERVOX_MAX_AUDIOS.get() + " audios!");
        if (this.mob.getCurrentAudioPlayer() != null && this.mob.getCurrentAudioPlayer().isPlaying()) return;

        List<Player> nearbyPlayers = new ArrayList<>(this.mob.level().
                getNearbyPlayers(TargetingConditions.forNonCombat(), this.mob, this.mob.getBoundingBox()
                        .inflate(CHANNEL_DISTANCE)));

        RevervoxMod.LOGGER.debug("Nearby Players: " + Arrays.toString(nearbyPlayers.toArray()));

        if (nearbyPlayers.isEmpty()) {
            RevervoxMod.LOGGER.debug("No players nearby");

            Level level = this.mob.level();
            Player nearestPlayer = null;
            for (Player player : Objects.requireNonNull(level.getServer()).getPlayerList().getPlayers()){
                if (!player.level().equals(this.mob.level())) continue;
                if (nearestPlayer != null){
                    nearestPlayer = this.mob.distanceToSqr(player) < this.mob.distanceToSqr(nearestPlayer) ? player : nearestPlayer;
                } else {
                    nearestPlayer = player;
                }
            }

            if (nearestPlayer != null) {
                if (this.mob.teleportTowards(nearestPlayer)){
                    RevervoxMod.LOGGER.debug("Teleporting towards nearest player: " + nearestPlayer.getName());
                    this.mob.playPlayerAudio(nearestPlayer, VoiceChatRecording.vcApi, this::getChannel);
                    audiosPlayed++;
                }
            }
        } else {
            if (nearbyPlayers.size() <= 4 && nearbyPlayers.size() > 1) {
                RevervoxMod.LOGGER.debug("Atleast 2 players nearby");

                for (int i = 0; i < nearbyPlayers.size(); i++) {
                    for (int k = i + 1; k < nearbyPlayers.size(); k++) {
                        Player player1 = nearbyPlayers.get(i);
                        Player player2 = nearbyPlayers.get(k);
                        if (player1.distanceToSqr(player2) > (double) CHANNEL_DISTANCE /2) {
                            RevervoxMod.LOGGER.debug("Atleast 2 players with distance greater than " + CHANNEL_DISTANCE/2);
                            Player furthestPlayer = player1.distanceToSqr(this.mob) > player2.distanceToSqr(this.mob) ? player1 : player2;
                            this.mob.playPlayerAudio(furthestPlayer, VoiceChatRecording.vcApi, this::getChannel);
                            audiosPlayed++;
                            return;
                        }
                    }
                }
            }
            RevervoxMod.LOGGER.debug("Playing audio from random player that is not near...");
            Set<UUID> nearbyPlayerUUIDs = nearbyPlayers.stream().map(Player::getUUID).collect(Collectors.toSet());
            IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio((u) -> !nearbyPlayerUUIDs.contains(u), true);
            if(audio == null){
                audio = RevervoxMod.AUDIOS.getRandomAudio(true);
            }
            if(audio == null){ return; }
            this.mob.playAudio(audio.getAudio(), VoiceChatRecording.vcApi, getChannel(), new AudioEffect());
            audiosPlayed++;
        }
    }

    private static EntityAudioChannel createChannel(VoicechatServerApi api, UUID channelID, Entity nearestEntity) {
        EntityAudioChannel channel = api.createEntityAudioChannel(channelID, api.fromEntity(nearestEntity));
        if (channel == null) {
            RevervoxMod.LOGGER.error("Couldn't create channel");
            return null;
        }
        channel.setCategory(RevervoxMod.MOD_ID);
        channel.setDistance(RandomRepeatGoal.CHANNEL_DISTANCE);
        return channel;
    }

    private Runnable setCanSpeak() {
        return () -> {
            if (this.mob.isAlive()){
                canSpeak = true;
                int ticksToSpeak = new Random().nextInt(5*20,10*20);

                RevervoxMod.TASKS.schedule(setCanSpeak(), ticksToSpeak);
            }
        };
    }


}
