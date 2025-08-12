package com.example.revervoxmod.entity.custom;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.voicechat.RecordedPlayer;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.voicechat.audio.AudioEffect;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.NotImplementedException;

public interface SpeakingEntity {
    public AudioPlayer getCurrentAudioPlayer();
    public void setCurrentAudioPlayer(AudioPlayer player);

    public default boolean hasSpoken(){
        throw new NotImplementedException("hasSpoken");
    }

    public default long getFirstSpoken(){
        throw new NotImplementedException("getFirstSpoken");
    }

    public void onSpeak(long audioDuration);

    public default void playAudio(short[] audio, VoicechatServerApi api, AudioChannel channel, AudioEffect effect){
        short[] audioWithAppliedEffects = effect.applyEffects(audio);
        setCurrentAudioPlayer(new AudioPlayer(audioWithAppliedEffects, api, channel));
        getCurrentAudioPlayer().start();
        final int SAMPLE_RATE = 48000;
        onSpeak((audio.length / SAMPLE_RATE) * 1000);
    }

    public default void playPlayerAudio(Player player, VoicechatServerApi api, AudioChannel channel){
        playPlayerAudio(player, api, channel, new AudioEffect());
    }

    public default void playPlayerAudio(Player player, VoicechatServerApi api, AudioChannel channel, AudioEffect effect){
        RecordedPlayer record = RevervoxVoicechatPlugin.getRecordedPlayer(player.getUUID());
        if (record == null) return;
        short[] audio = record.getRandomAudio(true);
        if(audio == null){
            RevervoxMod.LOGGER.error("No audio found for {}, choosing random player", player.getName());
            audio = RevervoxVoicechatPlugin.getRandomAudio(true);
            if (audio == null) return;
        }
        RevervoxMod.LOGGER.debug("Playing audio from player: " + player.getName());
        playAudio(audio, api, channel, effect);
    }

}
