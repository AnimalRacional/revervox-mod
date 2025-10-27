package dev.omialien.revervoxmod.entity.custom;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechatrecording.voicechat.audio.AudioPlayer;
import dev.omialien.voicechatrecording.api.AudioEffect;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

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

    default void playAudio(short @NotNull [] audio, VoicechatServerApi api, AudioChannel channel, AudioEffect effect){
        final int SAMPLE_RATE = 48000;
        short[] audioWithAppliedEffects = effect.applyEffects(audio);
        RevervoxMod.LOGGER.debug("Playing audio with {}s", audio.length / SAMPLE_RATE);
        setCurrentAudioPlayer(new AudioPlayer(audioWithAppliedEffects, api, channel));
        getCurrentAudioPlayer().start();
        onSpeak((audio.length / SAMPLE_RATE) * 1000);
    }

    default void playPlayerAudio(Player player, VoicechatServerApi api, Supplier<AudioChannel> channelSupp){
        playPlayerAudio(player, api, channelSupp, new AudioEffect());
    }

    default void playPlayerAudio(Player player, VoicechatServerApi api, Supplier<AudioChannel> channelSupp, AudioEffect effect){
        IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(player.getUUID(), true);
        if(audio == null){
            RevervoxMod.LOGGER.error("No audio found for {}, choosing random player", player.getName());
            audio = RevervoxMod.AUDIOS.getRandomAudio(true);
            if (audio == null) return;
        }
        AudioChannel channel = channelSupp.get();
        if(channel == null){ return; }
        RevervoxMod.LOGGER.debug("Playing audio from player: " + player.getName());
        playAudio(audio.getAudio(), api, channel, effect);
    }
}
