package com.example.revervoxmod.entity.goals;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
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
        if (audiosPlayed >= 5) this.mob.remove(Entity.RemovalReason.DISCARDED);
        RevervoxMod.LOGGER.info("Less than 5 audios!");
        if (this.mob.getCurrentAudioPlayer() != null && this.mob.getCurrentAudioPlayer().isPlaying()) return;
        if (RevervoxMod.vcApi instanceof VoicechatServerApi api){

            List<Player> nearbyPlayers = new ArrayList<>(this.mob.level().
                    getNearbyPlayers(TargetingConditions.DEFAULT, this.mob, this.mob.getBoundingBox()
                    .inflate(CHANNEL_DISTANCE)));

            RevervoxMod.LOGGER.info("Nearby Players: " + Arrays.toString(nearbyPlayers.toArray()));

            if (nearbyPlayers.isEmpty()) {
                RevervoxMod.LOGGER.info("No players nearby");

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
                    RevervoxMod.LOGGER.info("Teleporting towards nearest player: " + nearestPlayer.getName());
                    RevervoxMod.LOGGER.info("TeleportTowards: " + this.mob.teleportTowards(nearestPlayer));
                    audiosPlayed = 0;
                }
            } else {
                if (nearbyPlayers.size() <= 4 && nearbyPlayers.size() > 1) {
                    RevervoxMod.LOGGER.info("Atleast 2 players nearby");

                    for (int i = 0; i < nearbyPlayers.size(); i++) {
                        for (int k = i + 1; k < nearbyPlayers.size(); k++) {
                            Player player1 = nearbyPlayers.get(i);
                            Player player2 = nearbyPlayers.get(k);
                            if (player1.distanceToSqr(player2) > (double) CHANNEL_DISTANCE /2) {
                                RevervoxMod.LOGGER.info("Atleast 2 players with distance greater than " + CHANNEL_DISTANCE/2);
                                Player furthestPlayer = player1.distanceToSqr(this.mob) > player2.distanceToSqr(this.mob) ? player1 : player2;
                                this.mob.playAudio(furthestPlayer, api, channel);
                                audiosPlayed++;
                                //TODO dar set a canBeAngry para true depois de 1 segundo
                                return;
                            }
                        }
                    }
                }
                RevervoxMod.LOGGER.info("Playing audio from random player that is not near...");
                Set<UUID> recordedPlayers = RevervoxVoicechatPlugin.getRecordedPlayers().keySet();
                Set<UUID> nearbyPlayerUUIDs = nearbyPlayers.stream().map(Player::getUUID).collect(Collectors.toSet());
                Set<UUID> otherPlayers = new HashSet<>(recordedPlayers);
                otherPlayers.removeAll(nearbyPlayerUUIDs);

                if (!otherPlayers.isEmpty()) {
                    UUID randomUUID = new ArrayList<>(otherPlayers).get(new Random().nextInt(otherPlayers.size()));
                    this.mob.playAudio(Objects.requireNonNull(this.mob.level().getPlayerByUUID(randomUUID)), api, channel);
                    audiosPlayed++;
                    //TODO dar set a canBeAngry para true depois de 1 segundo
                } else {
                    RevervoxMod.LOGGER.info("No other players to play sounds from");
                }
            }
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
