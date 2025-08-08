package com.example.revervoxmod.voicechat;

import com.example.revervoxmod.RevervoxMod;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.*;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@ForgeVoicechatPlugin
public class RevervoxVoicechatPlugin implements VoicechatPlugin {
    public static String REVERVOX_CATEGORY = "revervox";
    private static HashMap<UUID, RecordedPlayer> recordedPlayers;
    private static ConcurrentHashMap<Path, Future<short[]>> audioCache;
    private static ConcurrentHashMap<UUID, List<short[]>> recordedAudios;

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
        RevervoxMod.LOGGER.info("Revervox voice chat plugin initialized!");
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

            recordedPlayer.setLastSpoke(new Date(System.currentTimeMillis()));
        }
    }

    private void onPlayerConnected(PlayerConnectedEvent e){
        UUID playerUuid = e.getConnection().getPlayer().getUuid();
        recordedPlayers.put(playerUuid, new RecordedPlayer(playerUuid));
    }

    private void onPlayerDisconnected(PlayerDisconnectedEvent e){
        stopRecording(e.getPlayerUuid());
        recordedPlayers.remove(e.getPlayerUuid());
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
        recordedPlayers = new HashMap<>();
        audioCache = new ConcurrentHashMap<>();
        recordedAudios = new ConcurrentHashMap<>();

        RevervoxMod.TASKS.schedule(checkForSilence(), 20);
    }

    public static void stopRecording(UUID uuid) {
        recordedPlayers.get(uuid).stopRecording();
        RevervoxMod.LOGGER.info("Stopped recording for player: " + uuid.toString());
    }

    public static void startRecording(UUID uuid) {
        recordedPlayers.get(uuid).startRecording();
        RevervoxMod.LOGGER.info("Recording started for player: " + uuid.toString());
    }

    public static RecordedPlayer getRecordedPlayer(UUID uuid) {
        return recordedPlayers.get(uuid);
    }

    public static HashMap<UUID, RecordedPlayer> getRecordedPlayers() {
        return recordedPlayers;
    }

    public static ConcurrentHashMap<Path, Future<short[]>> getAudioCache() {
        return audioCache;
    }

    public static void removeFromCache(Path path){
        audioCache.remove(path);
    }

    public static void addAudioToMem(UUID uuid, short[] audio){
        recordedAudios.get(uuid).add(audio);
    }

    private Runnable checkForSilence() {
        return () -> {
            long silenceThresholdMs = 1500;
            long now = System.currentTimeMillis();

            for (RecordedPlayer player : RevervoxVoicechatPlugin.getRecordedPlayers().values()) {
                if (player.isRecording() &&
                        (now - player.getLastSpoke().getTime()) > silenceThresholdMs) {
                    RevervoxMod.LOGGER.info("Stopped Speaking!");
                    RevervoxVoicechatPlugin.stopRecording(player.getUuid());
                }
            }

            // Re-schedule this same task to repeat
            RevervoxMod.TASKS.schedule(checkForSilence(), 20); // runs again in 20 ticks
        };
    }


}
