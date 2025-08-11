package com.example.revervoxmod.voicechat;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.config.RevervoxModServerConfigs;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ForgeVoicechatPlugin
public class RevervoxVoicechatPlugin implements VoicechatPlugin {
    public static String REVERVOX_CATEGORY = "revervox";
    private static Map<UUID, RecordedPlayer> recordedPlayers;
    private static Map<UUID, Boolean> privacyMode;
    /**
     * @return the unique ID for this voice chat plugin
     */
    @Override
    public String getPluginId() {
        return RevervoxMod.MOD_ID;
    }

    /**
     * Called when the voice chat initializes the plugin.
     *
     * @param api the voice chat API
     */
    @Override
    public void initialize(VoicechatApi api) {
        RevervoxMod.LOGGER.debug("Revervox voice chat plugin initialized!");
        RevervoxMod.vcApi = api;
    }

    /**
     * Called once by the voice chat to register all events.
     *
     * @param registration the event registration
     */
    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket, 100);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted, 100);
        registration.registerEvent(PlayerConnectedEvent.class, this::onPlayerConnected, 100);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::onPlayerDisconnected, 100);
    }

    private void onMicrophonePacket(MicrophonePacketEvent e){
        if (e.getSenderConnection() != null){ // If it's a player and not an entity
            RecordedPlayer recordedPlayer = recordedPlayers.get(e.getSenderConnection().getPlayer().getUuid());
            recordedPlayer.recordPacket(e.getPacket().getOpusEncodedData());
        }
    }

    private void onPlayerConnected(PlayerConnectedEvent e){
        UUID playerUuid = e.getConnection().getPlayer().getUuid();
        recordedPlayers.put(playerUuid, new RecordedPlayer(playerUuid));
        startRecording(playerUuid);
    }

    private void onPlayerDisconnected(PlayerDisconnectedEvent e){
        stopRecording(e.getPlayerUuid());
        recordedPlayers.get(e.getPlayerUuid()).saveAudios();
        recordedPlayers.remove(e.getPlayerUuid());
        privacyMode.remove(e.getPlayerUuid());
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        VoicechatServerApi api = event.getVoicechat();

        VolumeCategory revervoxCategory = api.volumeCategoryBuilder()
                .setId(REVERVOX_CATEGORY)
                .setName("Revervox")
                .setDescription("The volume of all monsters")
                .setIcon(null)
                .build();

        api.registerVolumeCategory(revervoxCategory);
        recordedPlayers = new ConcurrentHashMap<>();
        privacyMode = new ConcurrentHashMap<>();
        RevervoxMod.TASKS.schedule(checkForSilence(), 20);
    }

    public static void stopRecording(UUID uuid) {
        recordedPlayers.get(uuid).stopRecording();
        RevervoxMod.LOGGER.debug("Stopped recording for player: " + uuid.toString());
    }

    public static void startRecording(UUID uuid) {
        recordedPlayers.get(uuid).startRecording();
        RevervoxMod.LOGGER.debug("Recording started for player: " + uuid.toString());
    }

    public static RecordedPlayer getRecordedPlayer(UUID uuid) {
        return recordedPlayers.get(uuid);
    }

    public static Map<UUID, RecordedPlayer> getRecordedPlayers() {
        return recordedPlayers;
    }


    public static boolean getPrivacy(UUID uuid){
        return privacyMode.getOrDefault(uuid, false);
    }
    public static void setPrivacy(UUID uuid, boolean state){
        privacyMode.put(uuid, state);
    }
    public static short[] getAudio(UUID uuid, int idx, boolean remove){
        if(recordedPlayers.get(uuid).getAudioCount() > idx){
            return recordedPlayers.get(uuid).getAudio(idx, remove);
        }
        return null;
    }
    public static short[] getRandomAudio(UUID uuid, boolean remove){
        return recordedPlayers.get(uuid).getRandomAudio(remove);
    }
    public static short[] getRandomAudio(boolean remove){
        Random rnd = new Random();
        List<RecordedPlayer> players = recordedPlayers.values()
                .stream().filter((r) -> r.getAudioCount() > 0)
                .toList();
        if(players.isEmpty()){ return null; }
        return players.get(rnd.nextInt(players.size())).getRandomAudio(remove);
    }

    public static int getAudioCount(){
        return recordedPlayers.values().stream().map(RecordedPlayer::getAudioCount).reduce(0, Integer::sum);
    }
    // TODO atualmente todos os players têm a mesma chance de calhar para ser replaced, e não importa a quantidade de áudios que cada player tem
    // um player com 199 audios e outro com 1 vão ter a mesma chance de ser escolhidos para dar replace a um dos seus áudios
    public static void replaceRandomAudio(short[] audio){
        List<RecordedPlayer> hasAudio = recordedPlayers.values().stream().filter((r) -> r.getAudioCount() > 0).toList();
        RevervoxMod.LOGGER.debug("hasAudio: {}", hasAudio.size());
        if(hasAudio.isEmpty()){
            RevervoxMod.LOGGER.error("replaceRandomAudio called when no one has audios");
            return;
        }
        hasAudio.get((new Random()).nextInt(hasAudio.size())).replaceRandomAudio(audio);
    }

    public static void addAudio(UUID uuid, short[] audio){
        RecordedPlayer player = recordedPlayers.get(uuid);
        if(player == null){ return; }
        if(getAudioCount() >= RevervoxModServerConfigs.RECORDING_LIMIT.get()){
            replaceRandomAudio(audio);
        } else {
            player.addAudioDirect(audio);
        }
    }

    private Runnable checkForSilence() {
        return () -> {
            for (RecordedPlayer player : RevervoxVoicechatPlugin.getRecordedPlayers().values()) {
                if(player.isSpeaking()) continue;
                if (player.isSilent()) continue;
                RevervoxMod.LOGGER.debug("Stopped Speaking!");
                RevervoxVoicechatPlugin.stopRecording(player.getUuid());
                player.setSilent(true);
            }
            RevervoxMod.TASKS.schedule(checkForSilence(), 25);
        };
    }


}
