package dev.omialien.revervoxmod.voicechat.audio;

import dev.omialien.revervoxmod.RevervoxMod;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;

public class AudioPlayer extends Thread{
    private final VoicechatServerApi api;
    private final AudioChannel channel;
    private final short[] audio;
    private de.maxhenkel.voicechat.api.audiochannel.AudioPlayer playerAudioPlayer;

    public AudioPlayer(short[] audio, VoicechatServerApi api, AudioChannel channel) {
        this.audio = audio;
        this.api = api;
        this.channel = channel;
    }

    @Override
    public void run() {
        try{
            playerAudioPlayer = api.createAudioPlayer(channel, api.createEncoder(), audio);
            playerAudioPlayer.startPlaying();
            RevervoxMod.LOGGER.debug("Playing Audio...");
        } catch(Exception e){
            RevervoxMod.LOGGER.error("ERROR {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean isPlaying() {
        if (playerAudioPlayer == null) return false;
        return playerAudioPlayer.isPlaying();
    }
}
