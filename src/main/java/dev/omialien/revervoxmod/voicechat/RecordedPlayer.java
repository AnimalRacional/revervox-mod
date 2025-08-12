package dev.omialien.revervoxmod.voicechat;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.voicechat.audio.AudioReader;
import dev.omialien.revervoxmod.voicechat.audio.AudioSaver;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RecordedPlayer {
    public static Path audiosPath;
    private final Random rnd;
    public static final int RECORDING_SIZE = 1024*1024;
    private OpusDecoder decoder = null;
    private final short[] recording;
    private int currentRecordingIndex;
    private boolean isRecording = false;
    private final UUID uuid;
    private long lastSpoke;
    private static final long NOT_SPOKEN_YET = -1;
    private boolean isSilent = false;
    private final List<short[]> recordedAudios;
    public RecordedPlayer(UUID uuid) {
        rnd = new Random();
        this.uuid = uuid;
        this.recording = new short[RECORDING_SIZE];
        this.recordedAudios = new ArrayList<>();
        this.lastSpoke = NOT_SPOKEN_YET;
        RevervoxMod.LOGGER.debug("Created RecordedPlayer {}", uuid);
    }

    public void loadAudios(){
        Path userPath = audiosPath.resolve(this.uuid.toString());
        if(Files.exists(userPath)){
            RevervoxMod.LOGGER.debug("userpath exists");
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(userPath)){
                List<Future<short[]>> audios = new ArrayList<>();
                RevervoxMod.LOGGER.debug("getting audios...");
                for (Path cur : stream) {
                    String filename = cur.getFileName().toString();
                    RevervoxMod.LOGGER.debug("Reading {} {}/{} ({})", filename, filename.startsWith("audio-"), filename.endsWith(".pcm"), cur);
                    if(filename.startsWith("audio-") && filename.endsWith(".pcm")){
                        RevervoxMod.LOGGER.debug("Starting AudioReader for {}", cur);
                        AudioReader reader = new AudioReader(cur, true);
                        audios.add(reader.read());
                    } else {
                        RevervoxMod.LOGGER.warn("Unknown file {} in audio folder for {}, deleting", cur, uuid);
                        Files.delete(cur);
                    }
                }
                for(Future<short[]> cur : audios){
                    try{
                        RevervoxVoicechatPlugin.addAudio(uuid, cur.get());
                    } catch(InterruptedException e){
                        RevervoxMod.LOGGER.error("File reading interrupted");
                    } catch(ExecutionException e){
                        RevervoxMod.LOGGER.error("Execution Exception during file reading");
                    }
                }
                Files.delete(userPath);
            } catch(IOException e){
                RevervoxMod.LOGGER.error("Error reading audios for {}:\r\n{}\r\n{}", uuid, e.getMessage(), e.getStackTrace());
            }
        }
    }

    private boolean savingaudios = false;
    public void saveAudios(){
        if(!RevervoxVoicechatPlugin.getPrivacy(uuid) && !savingaudios){ // This method should only ever happen once per RecordedPlayer, no more no less
            savingaudios = true;
            Path userPath = audiosPath.resolve(this.uuid.toString());
            try{
                if(!Files.exists(userPath)){
                    Files.createDirectory(userPath);
                }
                for(int i = 0; i < recordedAudios.size(); i++){
                    short[] cur = recordedAudios.get(i);
                    new AudioSaver(userPath.resolve("audio-" + i + ".pcm"), cur.length, cur).start();
                }
            } catch(IOException e){
                RevervoxMod.LOGGER.error("Error saving audios for {}:\r\n{}\r\n{}", uuid, e.getMessage(), e.getStackTrace());
            }
        }
    }

    public void replaceRandomAudio(short[] audio){
        recordedAudios.set(rnd.nextInt(recordedAudios.size()), audio);
    }
    // This method should rarely be used, use RevervoxVoiceChatPlugin.addAudio(UUID, short[]) instead
    public void addAudioDirect(short[] audio){
        this.recordedAudios.add(audio);
    }

    public void stopRecording() {
        if (isRecording){
            /*
            isRecording = false;

            if (this.decoder != null) {
                this.decoder.close();
            }

             */
            if (filterAudio()){
                short[] savedRecording = new short[currentRecordingIndex];
                System.arraycopy(recording, 0, savedRecording, 0, currentRecordingIndex);
                RevervoxVoicechatPlugin.addAudio(uuid, savedRecording);
                RevervoxMod.LOGGER.debug("Added audio to MEMORY for player: " + uuid.toString());
            } else {
                RevervoxMod.LOGGER.debug("Audio filtered, not storing");
            }
            currentRecordingIndex = 0;
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
                    boolean active = false;
                    for (short value : decodedPacket) {
                        if (Math.abs(value) >= RevervoxModServerConfigs.SILENCE_THRESHOLD.get()) {
                            setLastSpoke(System.currentTimeMillis());
                            setSilent(false);
                            active = true;
                            break;
                        }
                    }
                    if(!active && currentRecordingIndex < 5){
                        return;
                    }
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

    public short[] removeAudio(int idx){
        short[] audio = recordedAudios.get(idx);
        recordedAudios.set(idx, recordedAudios.get(recordedAudios.size()-1));
        recordedAudios.remove(recordedAudios.size()-1);
        return audio;
    }

    public short[] getAudio(int idx, boolean remove){
        RevervoxMod.LOGGER.debug("getting audio {}: {}", idx, recordedAudios.get(idx).length);
        if(idx < 0 || idx >= recordedAudios.size()){
            return null;
        }
        if(remove){
            RevervoxMod.LOGGER.debug("removing audio {}", idx);
            return removeAudio(idx);
        }
        return recordedAudios.get(idx);
    }
    public short[] getRandomAudio(boolean remove){
        if(recordedAudios.isEmpty()){ return null; }
        int i = rnd.nextInt(recordedAudios.size());
        return getAudio(i, remove);
    }

    public int getAudioCount(){
        return recordedAudios.size();
    }

    public void startRecording() {
        if (!isRecording) {
            decoder = RevervoxMod.vcApi.createDecoder();
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
        return getLastSpoke() != NOT_SPOKEN_YET && System.currentTimeMillis() - getLastSpoke() < 2000;
    }

    public long getLastSpoke() {
        return lastSpoke;
    }

    public void setLastSpoke(long lastSpoke) {
        this.lastSpoke = lastSpoke;
    }

    private boolean filterAudio() {
        final int SAMPLE_RATE = 48000;
        final double MIN_DURATION = 0.9;
        final double MAX_DURATION = 10;
        final double MIN_RMS = 500;      // loudness threshold

        double durationSeconds = (double) currentRecordingIndex / SAMPLE_RATE;
        if (durationSeconds <= MIN_DURATION) {
            RevervoxMod.LOGGER.debug("Audio too short: " + durationSeconds + "s");
            return false;
        }
        if (durationSeconds > MAX_DURATION) {
            RevervoxMod.LOGGER.debug("Audio too long: " + durationSeconds + "s");
            return false;
        }

        int start = 0;
        while (start < currentRecordingIndex &&
                Math.abs(recording[start]) < RevervoxModServerConfigs.SILENCE_THRESHOLD.get()) {
            start++;
        }

        int end = currentRecordingIndex - 1;
        while (end > start &&
                Math.abs(recording[end]) < RevervoxModServerConfigs.SILENCE_THRESHOLD.get()) {
            end--;
        }

        int activeSamples = end - start + 1;
        if (activeSamples <= 0) {
            RevervoxMod.LOGGER.debug("No active audio found above silence threshold");
            return false;
        }

        // RMS on trimmed region
        long sumSquares = 0;
        for (int i = start; i <= end; i++) {
            int sample = recording[i];
            sumSquares += sample * sample;
        }
        double rms = Math.sqrt(sumSquares / (double) activeSamples);

        RevervoxMod.LOGGER.debug(String.format(
                "Audio duration: %.3fs, Active region: %.3fs, RMS: %.1f",
                durationSeconds,
                (double) activeSamples / SAMPLE_RATE,
                rms
        ));

        return rms >= MIN_RMS;
    }


    public boolean isSilent() {
        return isSilent;
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }
}
