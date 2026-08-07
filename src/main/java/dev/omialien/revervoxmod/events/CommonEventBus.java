package dev.omialien.revervoxmod.events;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.commands.RevervoxBatPeekCommand;
import dev.omialien.revervoxmod.commands.RevervoxCooldownCommand;
import dev.omialien.revervoxmod.commands.SummonFakeEntityCommand;
import dev.omialien.revervoxmod.commands.TriggerRevervoxBehindEventCommand;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.RevervoxCooldownManager;
import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import dev.omialien.revervoxmod.items.TapeItem;
import dev.omialien.revervoxmod.items.TapeRecorderItem;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.RevervoxTags;
import dev.omialien.revervoxmod.util.AudioUtil;
import dev.omialien.revervoxmod.util.PlayerVisibilityUtil;
import dev.omialien.revervoxmod.voicechat.AudioStorage;
import dev.omialien.revervoxmod.voicechat.PlayerStateManager;
import dev.omialien.revervoxmod.worldgen.biome.EchoDarkBiomeHandler;
import dev.omialien.revervoxmod.worldgen.biome.RevervoxBiomes;
import dev.omialien.voicechatrecording.api.AudioId;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.events.AudioLoadedEvent;
import dev.omialien.voicechatrecording.api.events.AudioRecordedEvent;
import dev.omialien.voicechatrecording.api.events.MicPacketReceivedEvent;
import dev.omialien.voicechatrecording.api.events.RecordingSetupEvent;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID)
public class CommonEventBus {
    @SubscribeEvent
    public static void tickEvent(TickEvent.ServerTickEvent event){
        RevervoxMod.TASKS.tick();
    }

    @SubscribeEvent
    public static void revervoxBatSpawnEvent(MobSpawnEvent.FinalizeSpawn event){
        if (event.getEntity() instanceof Bat && !event.getLevel().isClientSide()) {
            Position entityPos = event.getEntity().position();
            if (!event.getLevel().getBlockState(BlockPos.containing(entityPos)).getFluidState().is(Fluids.WATER)){
                if(new Random().nextInt(RevervoxModServerConfigs.REVERVOX_BAT_SPAWN_CHANCE.get()) == 0){
                    RevervoxBatGeoEntity bat = new RevervoxBatGeoEntity(EntityRegistry.REVERVOX_BAT.get(), event.getLevel().getLevel());
                    bat.moveTo(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
                    RevervoxMod.LOGGER.debug("Spawning Revervox Bat! at " + event.getEntity().getX() + ", " + event.getEntity().getY() + ", " + event.getEntity().getZ());
                    event.setSpawnCancelled(true);
                    event.getLevel().addFreshEntity(bat);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer observer
                && event.getTarget() instanceof ServerPlayer
                && observer.getTags().contains("revervox_behind_event_target")) {

            int targetId = event.getTarget().getId();
            ChunkMap chunkMap = observer.serverLevel().getChunkSource().chunkMap;
            ChunkMap.TrackedEntity trackedEntity = chunkMap.entityMap.get(targetId);

            if (trackedEntity != null) {
                trackedEntity.removePlayer(observer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getTags().contains("revervox_behind_event_target")) {
                PlayerVisibilityUtil.restorePlayerVision(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getTags().contains("revervox_behind_event_target")) {
                PlayerVisibilityUtil.restorePlayerVision(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer newPlayer) {
            if (newPlayer.getTags().contains("revervox_behind_event_target")) {
                PlayerVisibilityUtil.restorePlayerVision(newPlayer);
            }
        }
    }


    @SubscribeEvent
    public static void onRegisterEvents(ServerStartingEvent event) {
        int BatEventTime = new Random().nextInt((int) (12000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get()),
                (int) (24000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get()));
        RevervoxMod.LOGGER.debug("Scheduling bat for {} ticks", BatEventTime);
        RevervoxMod.TASKS.schedule(fakeBatEventSpawnRequest(
                event.getServer().getLevel(Level.OVERWORLD)), BatEventTime);
        int RevervoxBehindEventTime = new Random().nextInt((int) (20000 * RevervoxModServerConfigs.FAKE_REVERVOX_BEHIND_EVENT_CHANCE.get()),
                (int) (30000 * RevervoxModServerConfigs.FAKE_REVERVOX_BEHIND_EVENT_CHANCE.get()));
        RevervoxMod.LOGGER.debug("Scheduling revervox behind for {} ticks", RevervoxBehindEventTime);
        RevervoxMod.TASKS.schedule(fakeRevervoxBehindEventRequest(
                event.getServer().getLevel(Level.OVERWORLD)), RevervoxBehindEventTime);
        RevervoxMod.COOLDOWN = new RevervoxCooldownManager();
        EchoDarkBiomeHandler.startup();
    }

    private static Runnable fakeBatEventSpawnRequest(ServerLevel level){
        return () -> {
            RevervoxMod.LOGGER.debug("Starting fake bat event!");
            if(RevervoxModServerConfigs.ENABLE_FAKE_BAT_EVENT.get()) {
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
            }
            int nextRandomTick = new Random().nextInt((int) (12000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get()),(int) (24000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get())); //20 minutos max
            RevervoxMod.LOGGER.debug("next bat event scheduled for {} ticks", nextRandomTick);
            RevervoxMod.TASKS.schedule(fakeBatEventSpawnRequest(level), nextRandomTick);
        };
    }

    private static Runnable fakeRevervoxBehindEventRequest(ServerLevel level){
        return () -> {
            RevervoxMod.LOGGER.debug("Starting fake revervox behind event!");
            if(RevervoxModServerConfigs.ENABLE_FAKE_REVERVOX_BEHIND_EVENT.get()) {
                List<ServerPlayer> playerList = level.getServer().getPlayerList().getPlayers();
                if (playerList.size() > 1) {
                    int randomPlayer = new Random().nextInt(playerList.size());
                    if ((playerList.get(randomPlayer).level().equals(level)) && (playerList.get(randomPlayer).getY() < level.getSeaLevel() - 25)) {
                        ServerPlayer otherPlayer = (ServerPlayer) playerList.get(randomPlayer).level().getNearestPlayer(playerList.get(randomPlayer), 35);
                        if (otherPlayer == null) {
                            RevervoxMod.TASKS.schedule(fakeRevervoxBehindEventRequest(level), 600); // volta a tentar em 30 segundos
                            RevervoxMod.LOGGER.debug("Player didn't have a second player nearby, retrying in 30 secs!");
                            return;
                        }
                        RevervoxMod.LOGGER.debug("Player met requirements, starting revervox behind event!");
                        RevervoxMod.triggerRevervoxBehindEvent(playerList.get(randomPlayer));
                    } else {
                        RevervoxMod.LOGGER.debug("Player didn't meet requirements, skipping revervox behind event!");
                    }
                } else {
                    RevervoxMod.LOGGER.debug("(revervox behind Event) playerList is below 2 players");
                }
            }
            int nextRandomTick = new Random().nextInt((int) (20000 * RevervoxModServerConfigs.FAKE_REVERVOX_BEHIND_EVENT_CHANCE.get()),(int) (30000 * RevervoxModServerConfigs.FAKE_REVERVOX_BEHIND_EVENT_CHANCE.get()));
            RevervoxMod.LOGGER.debug("next revervox behind event scheduled for {} ticks", nextRandomTick);
            RevervoxMod.TASKS.schedule(fakeRevervoxBehindEventRequest(level), nextRandomTick);
        };
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SummonFakeEntityCommand.register(event.getDispatcher());
        TriggerRevervoxBehindEventCommand.register(event.getDispatcher());
        RevervoxCooldownCommand.register(event.getDispatcher());
        RevervoxBatPeekCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onRecordingApiInitialized(RecordingSetupEvent event) {
        RevervoxMod.RECORDING_API = event.getApi();
        event.addCategory(RevervoxMod.MOD_ID, "Revervox", "The volume of monsters", null);
        RevervoxMod.AUDIOS = new AudioStorage();
    }

    @SubscribeEvent
    public static void onAudioRecordedEvent(AudioRecordedEvent event){
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
    public static void onAudioLoadedEvent(AudioLoadedEvent event){
        if(RevervoxMod.AUDIOS.getTotalAudioCount() < RevervoxModServerConfigs.RECORDING_LIMIT.get() &&
                event.getLoadReason() == AudioLoadedEvent.LoadType.NAMESPACE &&
                event.getNamespace().equals(RevervoxMod.MOD_ID)) {
            RevervoxMod.LOGGER.debug("Storing namespace-loaded audio");
            RevervoxMod.AUDIOS.addAudio(event.getAudio());
        }
    }

    @SubscribeEvent
    public static void onMicrophonePacket(MicPacketReceivedEvent event){
        if (event.getPlayer() == null) return;
        short[] packet = PlayerStateManager.getPlayerDecoder(event.getPlayer().getUUID()).decode(event.getPacket().getOpusEncodedData());
        double packetRMS = AudioUtil.calculateRMS(packet);
        if (packetRMS > 4000.0D){
            PlayerStateManager.addScreamingPlayer(event.getPlayer().getUUID());
        } else {
            PlayerStateManager.removeScreamingPlayer(event.getPlayer().getUUID());
        }
        if (event.getPlayer().isUsingItem()
                && event.getPlayer().getUseItem().is(ItemRegistry.MEGAPHONE.get())
                && !Double.isNaN(packetRMS)
        ) {
            RevervoxMod.LOGGER.debug("Packet RMS: " + packetRMS);
            event.getPacket().setOpusEncodedData(
                    PlayerStateManager.getPlayerEncoder(event.getPlayer().getUUID()).encode(AudioUtil.applyRadioEffect(packet, 50)));
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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player instanceof ServerPlayer sp) {
            EchoDarkBiomeHandler.tickBatDamage(sp);
        }
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        Holder<Biome> biome = level.getBiome(pos);
        boolean inEchoDark = biome.is(RevervoxBiomes.REVERVOX_BIOME);
        if (inEchoDark) {
            if (!event.side.isClient()) {
                EchoDarkBiomeHandler.tickBiomeLogic(player);
            }
        }
    }


    @SubscribeEvent
    public static void repeatOnKill(LivingDeathEvent e){
        if(e.getEntity() instanceof ServerPlayer plr){
            if(e.getEntity().level() instanceof ServerLevel level){

                ItemStack weapon = ItemStack.EMPTY;

                Entity directEntity = e.getSource().getDirectEntity();
                if (directEntity instanceof LivingEntity living) {
                    weapon = living.getMainHandItem();
                }

                if (!weapon.isEmpty() && weapon.is(RevervoxTags.Items.AUDIO_ON_KILL)) {
                    IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(plr.getUUID(), false);
                    if (audio != null) {
                        AudioPlayingUtil.playLocationalAudio(audio, e.getEntity().position(), level, RevervoxMod.MOD_ID);
                    }
                }
            }
        }
    }
}
