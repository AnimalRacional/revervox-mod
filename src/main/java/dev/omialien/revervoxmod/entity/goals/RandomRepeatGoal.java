package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.stream.Collectors;

public class RandomRepeatGoal extends Goal {
    private final RevervoxGeoEntity mob;
    public static final int CHANNEL_DISTANCE = 30;
    private int audiosPlayed = 0;
    private int unheardAudios = 0;
    private long nextAudioAllowed = 0;
    public RandomRepeatGoal(RevervoxGeoEntity revervoxGeoEntity) {
        this.mob = revervoxGeoEntity;
    }

    private void setAudioCooldown(double duration) {
        this.nextAudioAllowed = this.mob.level().getGameTime() + ((int)(duration * 20)) + new Random().nextInt(5*20, 15*20);
    }

    @Override
    public boolean canUse() {
        return (this.mob.level().getGameTime() > nextAudioAllowed) && mob.getTarget() == null;
    }

    @Override
    public void stop() {
        super.stop();
    }

    public boolean canContinueToUse() {
        return false;
    }

    public void tick() {
        if (!(this.mob.level() instanceof ServerLevel level)) {
            return;
        }
        if (audiosPlayed >= RevervoxModServerConfigs.REVERVOX_MAX_AUDIOS.get()) {
            this.mob.remove(Entity.RemovalReason.DISCARDED);
            RevervoxMod.LOGGER.debug("Removed for max audios played");
            return;
        }

        List<Player> nearbyPlayers = new ArrayList<>(this.mob.level().
                getNearbyPlayers(TargetingConditions.forNonCombat().ignoreLineOfSight(), this.mob, this.mob.getBoundingBox()
                        .inflate(CHANNEL_DISTANCE)));

        if (nearbyPlayers.isEmpty()) {
            RevervoxMod.LOGGER.debug("No players nearby");
            this.nextAudioAllowed = this.mob.level().getGameTime() + 60;
            if (++unheardAudios >= RevervoxModServerConfigs.REVERVOX_UNHEARD_BEFORE_DISAPPEAR.get()) {
                this.mob.remove(Entity.RemovalReason.DISCARDED);
                RevervoxMod.LOGGER.debug("Unheard disappeared");
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
                            IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudioAnyFallback(furthestPlayer.getUUID(), true);
                            if (audio == null) {
                                return;
                            }
                            AudioPlayingUtil.playLocationalAudio(audio, this.mob.getEyePosition(), level, RevervoxMod.MOD_ID);
                            this.mob.onSpeak((int)(audio.getDuration() * 1000));
                            this.setAudioCooldown(audio.getDuration());
                            audiosPlayed++;
                            double range = RevervoxModServerConfigs.REVERVOX_COOLDOWN_RANGE.get();
                            if (range > 10000d) {
                                RevervoxMod.COOLDOWN.markSpawn(level.players().stream().map(Entity::getUUID).iterator(), level.getGameTime());
                            } else {
                                Iterator<UUID> players = this.mob.level().getNearbyPlayers(
                                        TargetingConditions.forNonCombat(),
                                        null,
                                        AABB.ofSize(this.mob.position(), range, range, range)
                                ).stream().map(Entity::getUUID).iterator();
                                RevervoxMod.COOLDOWN.markSpawn(players, level.getGameTime());
                            }
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
            double range = RevervoxModServerConfigs.REVERVOX_COOLDOWN_RANGE.get();
            if (range > 10000d) {
                RevervoxMod.COOLDOWN.markSpawn(level.players().stream().map(Entity::getUUID).iterator(), level.getGameTime());
            } else {
                Iterator<UUID> players = this.mob.level().getNearbyPlayers(
                        TargetingConditions.forNonCombat(),
                        null,
                        AABB.ofSize(this.mob.position(), range, range, range)
                ).stream().map(Entity::getUUID).iterator();
                RevervoxMod.COOLDOWN.markSpawn(players, level.getGameTime());
            }
            this.setAudioCooldown(audio.getDuration());
            AudioPlayingUtil.playFromEntity(audio, this.mob, RevervoxMod.MOD_ID);
            this.mob.onSpeak((int)(audio.getDuration() * 1000));
            audiosPlayed++;
        }
    }
}
