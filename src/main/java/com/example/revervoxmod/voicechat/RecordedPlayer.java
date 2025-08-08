package com.example.revervoxmod.voicechat;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.config.RevervoxModClientConfigs;
import com.example.revervoxmod.voicechat.audio.AudioSaver;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class RecordedPlayer {
    public static final int RECORDING_SIZE = 1024*1024;
    private OpusDecoder decoder = null;
    private final short[] recording;
    private int currentRecordingIndex;
    private int recordsCount;
    private boolean isRecording = false;
    private final UUID uuid;
    private final Path userPath;
    private Date lastSpoke;
    public static Path audiosPath;
    public static final int RECORDING_LIMIT = 50;

    public RecordedPlayer(UUID uuid) {
        this.uuid = uuid;
        this.recording = new short[RECORDING_SIZE];
        userPath = audiosPath.resolve(uuid.toString());
        recordsCount = 0;
        if(!Files.exists(userPath)){
            try {
                Files.createDirectory(userPath);
            } catch (IOException e) {
                RevervoxMod.LOGGER.error("Error creating directory " + userPath);
                return;
            }
        }
        try{
            // TODO isto le muitos ficheiros duma vez, talvez usar DirectoryStream
            File[] files = userPath.toFile().listFiles();
            if(files == null){
                RevervoxMod.LOGGER.error("User path {} not found!", userPath);
                throw new RuntimeException(String.format("User path %s not found!", userPath));
            }
            for(File f : files){
                String name = f.getName();
                if(name.startsWith(uuid.toString())){
                    String ending = name.substring(name.lastIndexOf('-')+1, name.lastIndexOf('.'));
                    try {
                        int num = Integer.parseInt(ending);
                        if(num > recordsCount){
                            recordsCount = num;
                        }
                    } catch(NumberFormatException e){
                        RevervoxMod.LOGGER.warn("Invalid audio found in folder {}", uuid);
                    }
                } else {
                    RevervoxMod.LOGGER.warn("Unknown audio found in folder {}", uuid);
                }
            }
        } catch(Exception e){
            RevervoxMod.LOGGER.error("error on recordedplayer: {}", e.getMessage());
        }
        updateRecordingCount();
    }

    private void updateRecordingCount(){
        recordsCount++;
        recordsCount %= RECORDING_LIMIT;
    }

    public Path getAudio(int index){
        return userPath.resolve(uuid.toString() + "-" + index + ".pcm");
    }

    public void stopRecording() {
        if (isRecording){
            isRecording = false;

            if (this.decoder != null) {
                this.decoder.close();
            }

            Path audioPath = userPath.resolve(getUuid().toString() + "-" + recordsCount + ".pcm");

            if (filterAudio(recording)){
                if (RevervoxModClientConfigs.PRIVACY_MODE.get()){
                    RevervoxVoicechatPlugin.addAudioToMem(uuid, recording);
                    RevervoxMod.LOGGER.info("Added audio to MEMORY for player: " + uuid.toString());
                } else {
                    new AudioSaver(audioPath, currentRecordingIndex, recording).start();
                }
            } else {
                RevervoxMod.LOGGER.info("Audio is smaller than 0.9 seconds, not saving.");
            }


            RevervoxVoicechatPlugin.removeFromCache(audioPath);
            updateRecordingCount();
        }
    }

    public void recordPacket(byte[] packet) {
        if (isRecording) {
            if (decoder == null) {
                RevervoxMod.LOGGER.warn("Decoder is not initialized!");
                return;
            }
            try {
                short[] decodedPacket = decoder.decode(packet);
                if (decodedPacket.length + currentRecordingIndex < RECORDING_SIZE){
                    System.arraycopy(decodedPacket, 0, recording, currentRecordingIndex, decodedPacket.length);
                    currentRecordingIndex += decodedPacket.length;
                } else {
                    RevervoxMod.LOGGER.warn("Recording buffer full!");
                    stopRecording();
                }
            } catch (Exception e) {
                RevervoxMod.LOGGER.error("Error decoding packet: {}", e.getMessage());
            }
        }
    }

    public void startRecording() { // WARNING: Poderá ser melhor reniciar a gravação em vez de continuar caso este método seja chamado múltiplas vezes
        if (!isRecording) {
            decoder = RevervoxMod.vcApi.createDecoder();
            currentRecordingIndex = 0;
            isRecording = true;
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isSpeaking() {
        if (this.getLastSpoke() == null) return false;
        return System.currentTimeMillis() - this.getLastSpoke().getTime() < 5000;
    }

    public Path getAudioPath(int index){
        Path audioPath = RecordedPlayer.audiosPath.resolve(uuid.toString()).resolve(uuid + "-" + index + ".pcm");
        RevervoxMod.LOGGER.info("Audio Path: " + audioPath);
        return audioPath;
    }

    public Path getRandomAudio(){
        if (recordsCount <= 1) return null;
        Path audioPath = RecordedPlayer.audiosPath.resolve(uuid.toString()).resolve(uuid + "-" + (new Random().nextInt(1, recordsCount)) + ".pcm");
        RevervoxMod.LOGGER.info("Audio Path: " + audioPath);
        return audioPath;
    }

    public Date getLastSpoke() {
        return lastSpoke;
    }

    public void setLastSpoke(Date lastSpoke) {
        this.lastSpoke = lastSpoke;
    }

    public static boolean filterAudio(short[] pcm) {
        final int SAMPLE_RATE = 48000; // Hz
        final int CHANNELS = 1;        // mono

        double durationSeconds = (double) pcm.length / (SAMPLE_RATE * CHANNELS);
        return durationSeconds > 0.9;
    }

}
