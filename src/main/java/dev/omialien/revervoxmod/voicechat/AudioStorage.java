package dev.omialien.revervoxmod.voicechat;

import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.voicechat_recording.voicechat.RecordedAudio;
import dev.omialien.voicechat_recording.voicechat.VoiceChatRecordingPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class AudioStorage {
    public static final int SAMPLE_RATE = 48000;
    private final Random rnd;
    private final Map<UUID, List<RecordedAudio>> storedAudios;

    public AudioStorage(){
        rnd = new Random();
        storedAudios = new ConcurrentHashMap<>();
    }

    public int getTotalAudioCount(){
        return storedAudios.values().stream().mapToInt(List::size).sum();
    }

    public void addAudio(RecordedAudio audio){
        if(!storedAudios.containsKey(audio.getPlayerUUID())){
            storedAudios.put(audio.getPlayerUUID(), new LinkedList<>());
        }
        storedAudios.get(audio.getPlayerUUID()).add(audio);
    }

    public RecordedAudio getRandomAudio(UUID player, boolean remove){
        List<RecordedAudio> recs = storedAudios.get(player);
        if(recs == null || recs.isEmpty()) { return null; }
        int idx = rnd.nextInt(recs.size());
        RecordedAudio audio = recs.get(idx);
        if(remove && getTotalAudioCount() > RevervoxModServerConfigs.MINIMUM_AUDIO_COUNT.get()){
            recs.remove(audio);
        }
        return audio;
    }

    public RecordedAudio getRandomAudio(Predicate<UUID> includePlayer, boolean remove){
        List<RecordedAudio> total = new LinkedList<>();
        storedAudios.keySet().stream().filter(includePlayer).forEach((uuid) -> {
            total.addAll(storedAudios.get(uuid));
        });
        if(total.isEmpty()){ return null; }
        int randomIndex = rnd.nextInt(total.size());
        RecordedAudio randomAudio = total.get(randomIndex);
        if(remove && getTotalAudioCount() > RevervoxModServerConfigs.MINIMUM_AUDIO_COUNT.get()){
            storedAudios.get(randomAudio.getPlayerUUID()).remove(randomAudio);
        }
        return randomAudio;
    }

    public RecordedAudio getRandomAudio(boolean remove){
        List<RecordedAudio> total = storedAudios.values().stream().flatMap(Collection::stream).toList();
        if(total.isEmpty()){ return null; }
        int randomIndex = rnd.nextInt(total.size());
        RecordedAudio randomAudio = total.get(randomIndex);
        if (remove && getTotalAudioCount() > RevervoxModServerConfigs.MINIMUM_AUDIO_COUNT.get()) {
            storedAudios.get(randomAudio.getPlayerUUID()).remove(randomAudio);
        }
        return randomAudio;
    }

    public void removeRandomAudio(){
        List<RecordedAudio> total = storedAudios.values().stream().flatMap(Collection::stream).toList();
        if(total.isEmpty()){ return; }
        RecordedAudio toRemove = total.get(rnd.nextInt(total.size()));
        storedAudios.get(toRemove.getPlayerUUID()).remove(toRemove);
    }

    public void savePlayerAudios(UUID uuid) {
        List<RecordedAudio> recs = storedAudios.get(uuid);
        if(recs != null && !recs.isEmpty()){
            recs.forEach(audio -> {
                if (audio.wasSaved()) return;
                audio.saveAudio();
            });
        }
    }

    public void loadPlayerAudios(UUID uuid) {
        VoiceChatRecordingPlugin.loadPlayerAudios(uuid);
    }
}
