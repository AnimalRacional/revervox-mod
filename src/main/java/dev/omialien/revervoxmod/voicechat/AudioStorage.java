package dev.omialien.revervoxmod.voicechat;

import dev.omialien.voicechat_recording.voicechat.RecordedAudio;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class AudioStorage {
    public static final int SAMPLE_RATE = 48000;
    private Random rnd;
    private Map<UUID, List<RecordedAudio>> storedAudios;

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

    // TODO parametro remove
    public RecordedAudio getRandomAudio(UUID player, boolean remove){
        List<RecordedAudio> recs = storedAudios.get(player);
        if(recs == null || recs.isEmpty()) { return null; }
        int idx = rnd.nextInt(recs.size());
        // TODO mover as configs da api para aqui
        //if(remove && getTotalAudioCount() > RevervoxModServerConfigs.)
        return recs.get(idx);
    }

    public RecordedAudio getRandomAudio(Predicate<UUID> includePlayer, boolean remove){
        List<RecordedAudio> total = new LinkedList<>();
        storedAudios.keySet().stream().filter(includePlayer).forEach((uuid) -> {
            total.addAll(storedAudios.get(uuid));
        });
        if(total.isEmpty()){ return null; }
        return total.get(rnd.nextInt(total.size()));
    }

    public RecordedAudio getRandomAudio(boolean remove){
        List<RecordedAudio> total = storedAudios.values().stream().flatMap(Collection::stream).toList();
        if(total.isEmpty()){ return null; }
        return total.get(rnd.nextInt(total.size()));
    }
}
