package com.example.revervoxmod.voicechat.audio;

import com.example.revervoxmod.RevervoxMod;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;

public class AudioPlayer extends Thread{
    private final VoicechatServerApi api;
    private final AudioChannel channel;
    private Mode mode = Mode.DEFAULT;
    private final short[] audio;
    private de.maxhenkel.voicechat.api.audiochannel.AudioPlayer playerAudioPlayer;
    public enum Mode{
        DEFAULT,
        PITCHED,
        REVERBED
    }
    public AudioPlayer(short[] audio, VoicechatServerApi api, AudioChannel channel) {
        this.audio = audio;
        this.api = api;
        this.channel = channel;
    }

    public AudioPlayer(short[] audio, VoicechatServerApi api, AudioChannel channel, Mode mode) {
        this.mode = mode;
        this.audio = audio;
        this.api = api;
        this.channel = channel;
    }

    @Override
    public void run() {
        short[] recording;
        try {
            recording = null;

            if (mode == Mode.DEFAULT) {
                recording = audio;
            } else if (mode == Mode.PITCHED) {
                recording = AudioManipulator.changePitch(audio, 0.8f);
            } else if (mode == Mode.REVERBED) {
                recording = AudioManipulator.addReverb(audio, 0.5f, 160, 3);
            }
            if (recording != null) {
                try{
                    playerAudioPlayer = api.createAudioPlayer(channel, api.createEncoder(), recording);
                    playerAudioPlayer.startPlaying();
                    RevervoxMod.LOGGER.debug("Playing Audio...");
                } catch(Exception e){
                    RevervoxMod.LOGGER.error("ERROR {}", e.getMessage());
                    throw new RuntimeException(e);
                }

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
