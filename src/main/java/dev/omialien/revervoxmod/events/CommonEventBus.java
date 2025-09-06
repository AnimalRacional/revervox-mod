package dev.omialien.revervoxmod.events;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.commands.SummonFakeEntityCommand;
import dev.omialien.revervoxmod.config.RevervoxModCommonConfigs;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.*;
import dev.omialien.revervoxmod.items.IRevervoxWeapon;
import dev.omialien.revervoxmod.networking.RevervoxClientPacketHandler;
import dev.omialien.revervoxmod.networking.packets.SoundInstancePacket;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.voicechat.AudioUtil;
import dev.omialien.revervoxmod.voicechat.PlayerStateManager;
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
        VoiceChatRecordingPlugin.addCategory(RevervoxMod.MOD_ID, "Revervox", "The volume of monsters", null);
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
        if (PlayerStateManager.isUsingMegaphone(event.getPlayer().getUUID())) {
            short[] packet = PlayerStateManager.getPlayerDecoder(event.getPlayer().getUUID()).decode(event.getPacket().getOpusEncodedData());
            double packetRMS = AudioUtil.calculateRMS(packet);
            RevervoxMod.LOGGER.debug("Packet RMS: " + packetRMS);
            if (packetRMS > 4000.0D){
                PlayerStateManager.addScreamingPlayer(event.getPlayer().getUUID());
            } else {
                PlayerStateManager.removeScreamingPlayer(event.getPlayer().getUUID());
            }
            event.getPacket().setOpusEncodedData(
                    PlayerStateManager.getPlayerEncoder(event.getPlayer().getUUID()).encode(AudioUtil.applyRadioEffect(packet, RevervoxModServerConfigs.VOICE_GAIN.get())));
        }
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
