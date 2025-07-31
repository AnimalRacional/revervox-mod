package com.example.revervoxmod.voicechat.audio;

import com.example.revervoxmod.RevervoxMod;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;

import java.nio.file.Path;

public class AudioPlayer extends Thread{
    private final Path path;
    private final VoicechatServerApi api;
    private final EntityAudioChannel channel;
    private de.maxhenkel.voicechat.api.audiochannel.AudioPlayer playerAudioPlayer;
    public AudioPlayer(Path path, VoicechatServerApi api, EntityAudioChannel channel) {
        this.path = path;
        this.api = api;
        this.channel = channel;
    }

    @Override
    public void run() {
        short[] recording;
        try {
            recording = new AudioReader(path).read().get();
            if (recording != null) {
                playerAudioPlayer = api.createAudioPlayer(channel, api.createEncoder(), recording);
                RevervoxMod.LOGGER.info("AudioPlayer Created");

                playerAudioPlayer.startPlaying();
                RevervoxMod.LOGGER.info("Playing Audio...");
            }
        } catch (Exception e) {
            RevervoxMod.LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public boolean isPlaying() {
        if (playerAudioPlayer == null) return false;
        return playerAudioPlayer.isPlaying();
    }
}
