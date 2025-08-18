package dev.omialien.revervoxmod.entity.custom;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechat_recording.voicechat.RecordedPlayer;
import dev.omialien.voicechat_recording.voicechat.VoiceChatRecordingPlugin;
import dev.omialien.voicechat_recording.voicechat.audio.AudioEffect;
import dev.omialien.voicechat_recording.voicechat.audio.AudioPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.NotImplementedException;

public interface SpeakingEntity {
    AudioPlayer getCurrentAudioPlayer();
    void setCurrentAudioPlayer(AudioPlayer player);

    default boolean hasSpoken(){
        throw new NotImplementedException("hasSpoken");
    }

    default long getFirstSpoken(){
        throw new NotImplementedException("getFirstSpoken");
    }

    void onSpeak(long audioDuration);

    default void playAudio(short[] audio, VoicechatServerApi api, AudioChannel channel, AudioEffect effect){
        short[] audioWithAppliedEffects = effect.applyEffects(audio);
        setCurrentAudioPlayer(new AudioPlayer(audioWithAppliedEffects, api, channel));
        getCurrentAudioPlayer().start();
        final int SAMPLE_RATE = 48000;
        onSpeak((audio.length / SAMPLE_RATE) * 1000);
    }

    default void playPlayerAudio(Player player, VoicechatServerApi api, AudioChannel channel){
        playPlayerAudio(player, api, channel, new AudioEffect());
    }

    default void playPlayerAudio(Player player, VoicechatServerApi api, AudioChannel channel, AudioEffect effect){
        RecordedPlayer record = VoiceChatRecordingPlugin.getRecordedPlayer(player.getUUID());
        if (record == null) return;
        short[] audio = record.getRandomAudio(true);
        if(audio == null){
            RevervoxMod.LOGGER.error("No audio found for {}, choosing random player", player.getName());
            audio = VoiceChatRecordingPlugin.getRandomAudio(true);
            if (audio == null) return;
        }
        RevervoxMod.LOGGER.debug("Playing audio from player: " + player.getName());
        playAudio(audio, api, channel, effect);
    }

}
