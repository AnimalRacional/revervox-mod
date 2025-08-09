package com.example.revervoxmod.voicechat;

import com.example.revervoxmod.RevervoxMod;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.*;

import java.util.*;

@ForgeVoicechatPlugin
public class RevervoxVoicechatPlugin implements VoicechatPlugin {
    public static String REVERVOX_CATEGORY = "revervox";
    private static HashMap<UUID, RecordedPlayer> recordedPlayers;

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

            recordedPlayer.setLastSpoke(new Date(System.currentTimeMillis()));
            recordedPlayer.setSilent(false);

        }
    }

    private void onPlayerConnected(PlayerConnectedEvent e){
        UUID playerUuid = e.getConnection().getPlayer().getUuid();
        recordedPlayers.put(playerUuid, new RecordedPlayer(playerUuid));
    }

    private void onPlayerDisconnected(PlayerDisconnectedEvent e){
        stopRecording(e.getPlayerUuid());
        recordedPlayers.get(e.getPlayerUuid()).saveAudios();
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

    public static HashMap<UUID, RecordedPlayer> getRecordedPlayers() {
        return recordedPlayers;
    }


    public static void setPrivacy(UUID uuid, boolean state){
        recordedPlayers.get(uuid).privacy = state;
    }
    public static short[] getAudio(UUID uuid, int idx){
        if(recordedPlayers.get(uuid).getAudioCount() > idx){
            return recordedPlayers.get(uuid).getAudio(idx);
        }
        return null;
    }
    public static short[] getRandomAudio(UUID uuid){
        return recordedPlayers.get(uuid).getRandomAudio();
    }
    public static short[] getRandomAudio(){
        Random rnd = new Random();
        List<RecordedPlayer> players = recordedPlayers.values()
                .stream().filter((r) -> r.getAudioCount() > 0)
                .toList();
        if(players.isEmpty()){ return null; }
        return players.get(rnd.nextInt(players.size())).getRandomAudio();
    }

    private Runnable checkForSilence() {
        return () -> {
            long silenceThresholdMs = 1100;
            long now = System.currentTimeMillis();

            for (RecordedPlayer player : RevervoxVoicechatPlugin.getRecordedPlayers().values()) {
                Date lastSpoke = player.getLastSpoke();
                if (lastSpoke == null) continue;
                if (player.isSilent()) continue;
                if (player.isRecording() &&
                        (now - lastSpoke.getTime()) > silenceThresholdMs) {
                    RevervoxMod.LOGGER.debug("Stopped Speaking!");
                    RevervoxVoicechatPlugin.stopRecording(player.getUuid());
                    player.setSilent(true);
                }
            }

            RevervoxMod.TASKS.schedule(checkForSilence(), silenceThresholdMs * 23);
        };
    }


}
