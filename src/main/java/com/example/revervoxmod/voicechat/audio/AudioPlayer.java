package com.example.revervoxmod.voicechat.audio;

import com.example.revervoxmod.RevervoxMod;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;

import java.nio.file.Path;

public class AudioPlayer extends Thread{
    private final Path path;
    private final VoicechatServerApi api;
    private final AudioChannel channel;
    private Mode mode = Mode.DEFAULT;
    private de.maxhenkel.voicechat.api.audiochannel.AudioPlayer playerAudioPlayer;
    public enum Mode{
        DEFAULT,
        PITCHED,
        REVERBED
    }
    public AudioPlayer(Path path, VoicechatServerApi api, AudioChannel channel) {
        this.path = path;
        this.api = api;
        this.channel = channel;
    }

    public AudioPlayer(Path path, VoicechatServerApi api, AudioChannel channel, Mode mode) {
        this.mode = mode;
        this.path = path;
        this.api = api;
        this.channel = channel;
    }

    @Override
    public void run() {
        short[] recording;
        try {
            recording = null;

            if (mode == Mode.DEFAULT) {
                recording = new AudioReader(path).read().get();
            } else if (mode == Mode.PITCHED) {
                recording = AudioManipulator.changePitch(new AudioReader(path).read().get(), 0.8f);
            } else if (mode == Mode.REVERBED) {
                recording = AudioManipulator.addReverb(new AudioReader(path).read().get(), 0.5f, 160, 3);
            }
            if (recording != null) {
                playerAudioPlayer = api.createAudioPlayer(channel, api.createEncoder(), recording);
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
