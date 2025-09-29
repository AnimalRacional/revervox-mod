package dev.omialien.revervoxmod.events;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.commands.SummonFakeEntityCommand;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.datagen.AdvancementProvider;
import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import dev.omialien.revervoxmod.entity.custom.RevervoxFakeBatEntity;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.entity.custom.ThingyEntity;
import dev.omialien.revervoxmod.networking.RevervoxClientPacketHandler;
import dev.omialien.revervoxmod.networking.packets.SoundInstancePacket;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.RevervoxTags;
import dev.omialien.revervoxmod.voicechat.AudioStorage;
import dev.omialien.revervoxmod.voicechat.AudioUtil;
import dev.omialien.revervoxmod.voicechat.PlayerStateManager;
import dev.omialien.voicechatrecording.voicechat.VoiceChatRecordingPlugin;
import dev.omialien.voicechatrecording.voicechat.util.AudioPlayingUtil;
import dev.omialien.voicechatrecording_api.IRecordedAudio;
import dev.omialien.voicechatrecording_api.events.AudioLoadedEvent;
import dev.omialien.voicechatrecording_api.events.AudioRecordedEvent;
import dev.omialien.voicechatrecording_api.events.MicPacketReceivedEvent;
import dev.omialien.voicechatrecording_api.events.RecordingSetupEvent;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
        //TODO RANDOM EVENT: se 2 players tiverem juntos, os dois param de ver um ao outro e
        // ouvem a voz do outro amigo atras deles, quando virarem se, levam com um jumpscare do
        // revervox e volta tudo ao normal. arranjar maneira de dar counter ao evento
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
    public static void onRecordingApiInitialized(RecordingSetupEvent event) {
        RevervoxMod.RECORDING_API = event.getApi();
        event.addCategory(RevervoxMod.MOD_ID, "Revervox", "The volume of monsters", null);
        RevervoxMod.AUDIOS = new AudioStorage();
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
    private static void onAudioRecordedEvent(AudioRecordedEvent event){
        if(event.getAudio().getFilterResult() == IRecordedAudio.FilterResult.PASSED){
            // TODO passar esta verificação para AudioStorage no addAudio
            if (RevervoxMod.AUDIOS.getTotalAudioCount() >= RevervoxModServerConfigs.RECORDING_LIMIT.get()){
                RevervoxMod.AUDIOS.removeRandomAudio();
            }
            RevervoxMod.LOGGER.debug("Audio recorded and stored!");
            RevervoxMod.AUDIOS.addAudio(event.getAudio());
        }
    }

    @SubscribeEvent
    private static void onAudioLoadedEvent(AudioLoadedEvent event){
        if(RevervoxMod.AUDIOS.getTotalAudioCount() < RevervoxModServerConfigs.RECORDING_LIMIT.get() &&
                event.getLoadReason() == VoiceChatRecordingPlugin.LoadType.NAMESPACE &&
                event.getNamespace().equals(RevervoxMod.MOD_ID)) {
            RevervoxMod.LOGGER.debug("Storing namespace-loaded audio");
            RevervoxMod.AUDIOS.addAudio(event.getAudio());
        }
    }

    @SubscribeEvent
    public static void onMicrophonePacket(MicPacketReceivedEvent event){
        if (event.getPlayer() == null) return;
        if (event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().is(ItemRegistry.MEGAPHONE)) {
            short[] packet = PlayerStateManager.getPlayerDecoder(event.getPlayer().getUUID()).decode(event.getPacket().getOpusEncodedData());
            double packetRMS = AudioUtil.calculateRMS(packet);
            if(!Double.isNaN(packetRMS)){
                RevervoxMod.LOGGER.debug("Packet RMS: " + packetRMS);
                event.getPacket().setOpusEncodedData(
                        PlayerStateManager.getPlayerEncoder(event.getPlayer().getUUID()).encode(AudioUtil.applyRadioEffect(packet, 50)));
            }
            if (packetRMS > 4000.0D){
                PlayerStateManager.addScreamingPlayer(event.getPlayer().getUUID());
            } else {
                PlayerStateManager.removeScreamingPlayer(event.getPlayer().getUUID());
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
        PlayerStateManager.removeState(event.getEntity().getUUID());
        RevervoxMod.AUDIOS.savePlayerAudios(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event){
        PlayerStateManager.createState(event.getEntity().getUUID());
        RevervoxMod.AUDIOS.loadPlayerAudios(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void repeatOnKill(LivingDeathEvent e){
        if(e.getEntity() instanceof ServerPlayer plr){
            if(e.getEntity().level() instanceof ServerLevel level){
                if(e.getSource().getWeaponItem() != null && e.getSource().getWeaponItem().is(RevervoxTags.Items.AUDIO_ON_KILL)) {
                    IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(plr.getUUID(), false);
                    if(audio != null){
                        AudioPlayingUtil.playLocationalAudio(audio, e.getEntity().position(), level, RevervoxMod.MOD_ID);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void GatherDataEvent(GatherDataEvent event){
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeServer(), new AdvancementProvider(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
    }
}
