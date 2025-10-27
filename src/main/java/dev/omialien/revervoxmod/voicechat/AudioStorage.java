package dev.omialien.revervoxmod.voicechat;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.voicechatrecording.api.IRecordedAudio;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AudioStorage {
    public static final int SAMPLE_RATE = 48000;
    private final Random rnd;
    private final Map<UUID, List<IRecordedAudio>> storedAudios;

    public AudioStorage(){
        rnd = new Random();
        storedAudios = new ConcurrentHashMap<>();
    }

    public int getTotalAudioCount(){
        return storedAudios.values().stream().mapToInt(List::size).sum();
    }

    public void addAudio(IRecordedAudio audio){
        if(!storedAudios.containsKey(audio.getPlayerUUID())){
            storedAudios.put(audio.getPlayerUUID(), new LinkedList<>());
        }
        audio.saveAudio(RevervoxMod.MOD_ID);
        storedAudios.get(audio.getPlayerUUID()).add(audio);
    }

    public IRecordedAudio getRandomAudio(UUID player, boolean remove){
        RevervoxMod.LOGGER.debug("storage: getting random audio specific, remove {} at size {}", remove, this.getTotalAudioCount());
        List<IRecordedAudio> recs = storedAudios.get(player);
        if(recs == null || recs.isEmpty()) { return null; }
        int idx = rnd.nextInt(recs.size());
        IRecordedAudio audio = recs.get(idx);
        if(remove && getTotalAudioCount() > RevervoxModServerConfigs.MINIMUM_AUDIO_COUNT.get()){
            RevervoxMod.LOGGER.debug("removing random audio from get");
            storedAudios.get(player).remove(audio);
            RevervoxMod.RECORDING_API.unsaveAudio(RevervoxMod.MOD_ID, audio);
        }
        return audio;
    }

    public IRecordedAudio getRandomAudio(Predicate<UUID> includePlayer, boolean remove){
        RevervoxMod.LOGGER.debug("storage: getting random audio preidcate, remove {} at size {}", remove, this.getTotalAudioCount());
        List<IRecordedAudio> total = new LinkedList<>();
        storedAudios.keySet().stream().filter(includePlayer).forEach((uuid) -> {
            total.addAll(storedAudios.get(uuid));
        });
        if(total.isEmpty()){ return null; }
        int randomIndex = rnd.nextInt(total.size());
        IRecordedAudio randomAudio = total.get(randomIndex);
        if(remove && getTotalAudioCount() > RevervoxModServerConfigs.MINIMUM_AUDIO_COUNT.get()){
            RevervoxMod.LOGGER.debug("removing random audio from get");
            storedAudios.get(randomAudio.getPlayerUUID()).remove(randomAudio);
            RevervoxMod.RECORDING_API.unsaveAudio(RevervoxMod.MOD_ID, randomAudio);
        }
        return randomAudio;
    }

    public IRecordedAudio getRandomAudio(boolean remove){
        RevervoxMod.LOGGER.debug("storage: getting random audio, remove {} at size {}", remove, this.getTotalAudioCount());
        List<IRecordedAudio> total = storedAudios.values().stream().flatMap(Collection::stream).toList();
        if(total.isEmpty()){ return null; }
        int randomIndex = rnd.nextInt(total.size());
        IRecordedAudio randomAudio = total.get(randomIndex);
        if (remove && getTotalAudioCount() > RevervoxModServerConfigs.MINIMUM_AUDIO_COUNT.get()) {
            RevervoxMod.LOGGER.debug("removing random audio from get");
            storedAudios.get(randomAudio.getPlayerUUID()).remove(randomAudio);
            RevervoxMod.RECORDING_API.unsaveAudio(RevervoxMod.MOD_ID, randomAudio);
        }
        return randomAudio;
    }

    public void removeRandomAudio(){
        RevervoxMod.LOGGER.debug("removing random audio method");
        List<IRecordedAudio> total = storedAudios.values().stream().flatMap(Collection::stream).toList();
        if(total.isEmpty()){ return; }
        IRecordedAudio toRemove = total.get(rnd.nextInt(total.size()));
        storedAudios.get(toRemove.getPlayerUUID()).remove(toRemove);
        RevervoxMod.RECORDING_API.unsaveAudio(RevervoxMod.MOD_ID, toRemove);
    }

    public void savePlayerAudios(UUID uuid) {
        List<IRecordedAudio> recs = storedAudios.get(uuid);
        if(recs != null && !recs.isEmpty()){
            recs.forEach(audio -> {
                audio.saveAudio(RevervoxMod.MOD_ID);
            });
        }
    }

    public void saveAudios() {
        Set<IRecordedAudio> audios = storedAudios.values().stream().flatMap(List::stream).collect(Collectors.toSet());
        for(IRecordedAudio audio : audios) {
            audio.saveAudio(RevervoxMod.MOD_ID);
        }
    }

    public void loadPlayerAudios(UUID uuid) {
        // TODO how to deal with lots of audios being loaded when the limit has already been reached?
        RevervoxMod.RECORDING_API.loadPlayerAudios(uuid, (audio) -> {
            if(RevervoxMod.AUDIOS.getTotalAudioCount() < RevervoxModServerConfigs.RECORDING_LIMIT.get()) {
                RevervoxMod.AUDIOS.addAudio(audio);
            }
        });
    }
}
