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

import java.util.*;
import java.util.stream.Collectors;

public class RandomRepeatGoal extends Goal {
    private final RevervoxGeoEntity mob;
    private static final int CHANNEL_DISTANCE = 30;
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


            if (nearbyPlayers.isEmpty()) {
                RevervoxMod.LOGGER.info("No players nearby");
                Player nearestPlayer = this.mob.level().getNearestPlayer(this.mob, CHANNEL_DISTANCE * 4);
                if (nearestPlayer != null) {
                    RevervoxMod.LOGGER.info("Teleporting towards nearest player");
                    this.mob.teleportTowards(nearestPlayer);
                    audiosPlayed = 0;
                }
            } else {
                if (nearbyPlayers.size() < 4 && nearbyPlayers.size() >1) {
                    for (int i = 0; i < nearbyPlayers.size() - 1; i++) {
                        Player player1 = nearbyPlayers.get(i);
                        Player player2 = nearbyPlayers.get(i + 1);
                        if (player1.distanceToSqr(player2) > CHANNEL_DISTANCE) {
                            this.mob.playAudio(player1, api, channel);
                            audiosPlayed++;
                            //TODO dar set a canBeAngry para true depois de 1 segundo
                            return;
                        }
                    }
                } else {
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
