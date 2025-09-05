package dev.omialien.revervoxmod.events;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.commands.SummonFakeEntityCommand;
import dev.omialien.revervoxmod.config.RevervoxModCommonConfigs;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.*;
import dev.omialien.revervoxmod.items.IRevervoxWeapon;
import dev.omialien.revervoxmod.networking.RevervoxClientPacketHandler;
import dev.omialien.revervoxmod.networking.packets.SoundInstancePacket;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.voicechat.PlayerStateManager;
import dev.omialien.voicechat_recording.VoiceChatRecording;
import dev.omialien.voicechat_recording.configs.RecordingCommonConfig;
import dev.omialien.voicechat_recording.voicechat.RecordedAudio;
import dev.omialien.voicechat_recording.voicechat.VoiceChatRecordingPlugin;
import dev.omialien.voicechat_recording.voicechat.events.AudioEvent;
import dev.omialien.voicechat_recording.voicechat.events.MicPacketReceivedEvent;
import dev.omialien.voicechat_recording.voicechat.util.AudioPlayingUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@EventBusSubscriber(modid = RevervoxMod.MOD_ID)
public class CommonEventBus {
    @SubscribeEvent
    public static void tickEvent(ServerTickEvent.Post event){
        RevervoxMod.TASKS.tick();
    }

    @SubscribeEvent
    public static void revervoxBatSpawnEvent(FinalizeSpawnEvent event){
        if (event.getEntity() instanceof Bat && !event.getLevel().isClientSide()) {
            if (new Random().nextInt(RevervoxModServerConfigs.REVERVOX_BAT_SPAWN_CHANCE.get()) == 0){
                RevervoxBatGeoEntity bat = new RevervoxBatGeoEntity(EntityRegistry.REVERVOX_BAT.get(), event.getLevel().getLevel());
                bat.moveTo(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
                RevervoxMod.LOGGER.debug("Spawning Revervox Bat! at " + event.getEntity().getX() + ", " + event.getEntity().getY() + ", " + event.getEntity().getZ());
                event.setSpawnCancelled(true);
                event.getLevel().addFreshEntity(bat);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterEvents(ServerStartingEvent event) {
        RevervoxMod.TASKS.schedule(fakeBatEventSpawnRequest(
                event.getServer().getLevel(Level.OVERWORLD)),
                new Random().nextInt((int) (12000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get()),
                        (int) (24000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get())));
    }

    private static Runnable fakeBatEventSpawnRequest(ServerLevel level){
        return () -> {
            RevervoxMod.LOGGER.debug("Starting fake bat event!");
            List<ServerPlayer> playerList = level.getServer().getPlayerList().getPlayers();
            if (!playerList.isEmpty()) {
                int randomPlayer = new Random().nextInt(playerList.size());
                if ((playerList.get(randomPlayer).level().equals(level)) && (playerList.get(randomPlayer).getY() < level.getSeaLevel() - 25)) {
                    RevervoxMod.LOGGER.debug("Player met requirements, starting bat event!");
                    RevervoxMod.summonBatWave(playerList.get(randomPlayer));
                } else {
                    RevervoxMod.LOGGER.debug("Player didn't meet requirements, skipping bat event!");
                }
            } else {
                RevervoxMod.LOGGER.debug("(Fake Bat Event) playerList is empty");
            }
            int nextRandomTick = new Random().nextInt((int) (12000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get()),(int) (24000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get())); //20 minutos max
            RevervoxMod.LOGGER.debug("next bat event scheduled for " + (nextRandomTick/20)/60 + " minutes");
            RevervoxMod.TASKS.schedule(fakeBatEventSpawnRequest(level), nextRandomTick);
        };
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SummonFakeEntityCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        VoiceChatRecordingPlugin.addCategory(RevervoxMod.MOD_ID, "Revervox", "The volume of monsters", null, (VoicechatServerApi) VoiceChatRecording.vcApi);
    }

    // TODO neoforge might have a better way of doing this
    @SubscribeEvent
    public static void onSpeakingEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof SpeakingEntity) {
            if(!entity.level().isClientSide()){
                entity.dropAllDeathLoot((ServerLevel) entity.level(), Objects.requireNonNull(event.getSource()));
            }
            event.setCanceled(true); // Prevent default death behavior
            entity.remove(Entity.RemovalReason.KILLED); // Disappear instantly
        }
    }

    // TODO neoforge might have a better way to do this
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event){
        if(!event.getEntity().level().isClientSide() && event.getEntity() instanceof Player victim){
            DamageSource source = event.getSource();
            RevervoxMod.LOGGER.debug("damage source: {}", source);
            RevervoxMod.LOGGER.debug("attacker entity: {}", source.getEntity());
            if(source.getEntity() == null){
                RevervoxMod.LOGGER.debug("no entity source");
                return;
            }
            if(source.getEntity() instanceof Player attacker){
                RevervoxMod.LOGGER.debug("is player && serverapi");
                if(attacker.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IRevervoxWeapon){
                    RecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(victim.getUUID(), false);
                    if(audio == null) { return; }
                    AudioPlayingUtil.playLocationalAudio(audio, victim.position(), (ServerLevel) victim.level(), RevervoxMod.MOD_ID);
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(EntityRegistry.THINGY.get(), ThingyEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX.get(), RevervoxGeoEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX_BAT.get(), RevervoxBatGeoEntity.createAttributes().build());
        event.put(EntityRegistry.REVERVOX_FAKE_BAT.get(), RevervoxFakeBatEntity.createAttributes().build());
    }
    @SubscribeEvent
    public static void registerSpawnPlacement(RegisterSpawnPlacementsEvent event) {
        event.register(EntityRegistry.REVERVOX.get(),
                SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RevervoxGeoEntity::checkRevervoxSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                SoundInstancePacket.TYPE,
                SoundInstancePacket.STREAM_CODEC,
                FMLEnvironment.dist == Dist.CLIENT ? RevervoxClientPacketHandler::handleSoundInstancePacket : null
        );
    }

    @SubscribeEvent
    private static void onAudioEvent(AudioEvent event){
        if(event.getAudio().getFilterResult() == RecordedAudio.FilterResult.PASSED){
            if (RevervoxMod.AUDIOS.getTotalAudioCount() >= RevervoxModCommonConfigs.RECORDING_LIMIT.get()){
                RevervoxMod.AUDIOS.removeRandomAudio();
            }
            RevervoxMod.LOGGER.debug("Audio recorded and stored!");
            RevervoxMod.AUDIOS.addAudio(event.getAudio());
        }
    }

    @SubscribeEvent
    public static void onMicrophonePacket(MicPacketReceivedEvent event){
        if (event.getPlayer() == null) return;
        short[] packet = PlayerStateManager.getPlayerDecoder(event.getPlayer().getUUID()).decode(event.getPacket().getOpusEncodedData());
        double packetRMS = calculateRMS(packet);
        RevervoxMod.LOGGER.debug("Packet RMS: " + packetRMS);
        if (packetRMS > 3000.0D){
            PlayerStateManager.addScreamingPlayer(event.getPlayer().getUUID());
        } else {
            PlayerStateManager.removeScreamingPlayer(event.getPlayer().getUUID());
        }
        // TODO isUsingMegaphone por voz radio
        /*
        event.getPacket().setOpusEncodedData(
                PlayerStateManager.getPlayerEncoder(event.getPlayer().getUUID()).encode(applyRadioEffect(packet, RevervoxModServerConfigs.VOICE_GAIN.get())));
         */
    }

    public static short[] applyRadioEffect(short[] pcmBE, double gain) {
        // Convert to float [-1, 1]
        float[] samples = new float[pcmBE.length];
        for (int i = 0; i < pcmBE.length; i++) {
            samples[i] = pcmBE[i] / 32768.0f;
        }

        // Simple band-pass FIR filter (300–3500 Hz @ 48kHz)
        float[] filtered = bandPassFilter(samples, 300.0, 3500.0, 48000);

        // Apply gain (loudness boost)
        short[] out = new short[filtered.length];
        for (int i = 0; i < filtered.length; i++) {
            float v = (float) (filtered[i] * gain);
            v = Math.max(-1.0f, Math.min(1.0f, v)); // prevent clipping
            out[i] = (short) (v * 32767);
        }

        return out;
    }

    // Very simple band-pass filter (FIR via naive convolution)
    private static float[] bandPassFilter(float[] input, double lowCut, double highCut, int sampleRate) {
        int filterSize = 101; // longer = sharper filter
        float[] filter = new float[filterSize];

        double nyquist = sampleRate / 2.0;
        double low = lowCut / nyquist;
        double high = highCut / nyquist;

        // Design a band-pass filter using windowed sinc
        for (int i = 0; i < filterSize; i++) {
            int m = i - filterSize / 2;
            if (m == 0) {
                filter[i] = (float) (2 * (high - low));
            } else {
                filter[i] = (float) ((Math.sin(2 * Math.PI * high * m) - Math.sin(2 * Math.PI * low * m)) / (Math.PI * m));
            }
            // Apply Hamming window
            filter[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (filterSize - 1));
        }

        // Convolution
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++) {
            double acc = 0;
            for (int j = 0; j < filterSize; j++) {
                int idx = i - j;
                if (idx >= 0) acc += input[idx] * filter[j];
            }
            output[i] = (float) acc;
        }

        return output;
    }

    public static double calculateRMS(short[] audio){
        int start;
        for(start = 0; start < audio.length && Math.abs(audio[start]) < (Integer) RecordingCommonConfig.SILENCE_THRESHOLD.get(); ++start) {
        }

        int end;
        for(end = audio.length - 1; end > start && Math.abs(audio[end]) < (Integer)RecordingCommonConfig.SILENCE_THRESHOLD.get(); --end) {
        }

        int activeSamples = end - start + 1;
        long sumSquares = 0L;

        for(int i = start; i <= end; ++i) {
            int sample = audio[i];
            sumSquares += (long)(sample * sample);
        }

        return Math.sqrt((double)sumSquares / (double)activeSamples);
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
        PlayerStateManager.removePlayerCoders(event.getEntity().getUUID());
        RevervoxMod.AUDIOS.savePlayerAudios(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event){
        PlayerStateManager.addPlayerCoders(event.getEntity().getUUID());
        RevervoxMod.AUDIOS.loadPlayerAudios(event.getEntity().getUUID());
    }
}
