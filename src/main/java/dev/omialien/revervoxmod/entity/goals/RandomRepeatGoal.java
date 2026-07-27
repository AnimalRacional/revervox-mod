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

import java.util.*;
import java.util.stream.Collectors;

public class RandomRepeatGoal extends Goal {
    private final RevervoxGeoEntity mob;
    public static final int CHANNEL_DISTANCE = 30;
    private int audiosPlayed = 0;
    private boolean canSpeak = true;
    public RandomRepeatGoal(RevervoxGeoEntity revervoxGeoEntity) {
        this.mob = revervoxGeoEntity;
        setCanSpeak().run();
    }
    @Override
    public boolean canUse() {
        return canSpeak && mob.getTarget() == null;
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
        canSpeak = false;
        if (audiosPlayed >= RevervoxModServerConfigs.REVERVOX_MAX_AUDIOS.get()) this.mob.remove(Entity.RemovalReason.DISCARDED);
        RevervoxMod.LOGGER.debug("Less than " + RevervoxModServerConfigs.REVERVOX_MAX_AUDIOS.get() + " audios!");
        // TODO it may be able to play an audio while another is still playing

        List<Player> nearbyPlayers = new ArrayList<>(this.mob.level().
                getNearbyPlayers(TargetingConditions.forNonCombat().ignoreLineOfSight(), this.mob, this.mob.getBoundingBox()
                        .inflate(CHANNEL_DISTANCE)));

        RevervoxMod.LOGGER.debug("Nearby Players: " + Arrays.toString(nearbyPlayers.toArray()));

        if (nearbyPlayers.isEmpty()) {
            RevervoxMod.LOGGER.debug("No players nearby");
            this.mob.remove(Entity.RemovalReason.DISCARDED);
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
                            AudioPlayingUtil.playLocationalAudio(audio, this.mob.getEyePosition(), level, RevervoxMod.MOD_ID);
                            this.mob.onSpeak((int)(audio.getDuration() * 1000));
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
            AudioPlayingUtil.playFromEntity(audio, this.mob, RevervoxMod.MOD_ID);
            this.mob.onSpeak((int)(audio.getDuration() * 1000));
            audiosPlayed++;
        }
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
