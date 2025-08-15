package dev.omialien.revervoxmod.events;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.commands.*;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import dev.omialien.revervoxmod.entity.custom.SpeakingEntity;
import dev.omialien.revervoxmod.items.IRevervoxWeapon;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.voicechat_recording.RecordingSimpleVoiceChat;
import dev.omialien.voicechat_recording.voicechat.RecordedPlayer;
import dev.omialien.voicechat_recording.voicechat.RecordingSimpleVoiceChatPlugin;
import dev.omialien.voicechat_recording.voicechat.audio.AudioPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class CommonForgeEventBus {
    @SubscribeEvent
    public void tickEvent(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            RevervoxMod.TASKS.tick();
        }
    }

    @SubscribeEvent
    public void revervoxBatSpawnEvent(MobSpawnEvent.FinalizeSpawn event){
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
    public void onRegisterEvents(ServerStartingEvent event) {
        RevervoxMod.TASKS.schedule(fakeBatEventSpawnRequest(
                event.getServer().getLevel(Level.OVERWORLD)),
                new Random().nextInt((int) (12000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get()),
                        (int) (24000 * RevervoxModServerConfigs.FAKE_BAT_EVENT_CHANCE.get())));
    }

    private Runnable fakeBatEventSpawnRequest(ServerLevel level){
        return () -> {
            RevervoxMod.LOGGER.debug("Starting fake bat event!");
            List<ServerPlayer> playerList = level.getServer().getPlayerList().getPlayers();
            if (!playerList.isEmpty()) {
                int randomPlayer = new Random().nextInt(playerList.size());
                if ((playerList.get(randomPlayer).level().equals(level)) && (playerList.get(randomPlayer).getY() < level.getSeaLevel())) {
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
    public void onRegisterCommands(RegisterCommandsEvent event) {
        SummonFakeEntityCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        RevervoxMod.LOGGER.debug("Server starting");
        RecordedPlayer.audiosPath = event.getServer().getWorldPath(RevervoxMod.AUDIO_DIRECTORY);
        if(!Files.exists(RecordedPlayer.audiosPath)){
            try {
                Files.createDirectory(RecordedPlayer.audiosPath);
            } catch (IOException e) {
                RevervoxMod.LOGGER.error("Error creating audios directory: " + e.getMessage());
            }
        }
        RecordingSimpleVoiceChatPlugin.addCategory(RevervoxMod.MOD_ID, "Revervox", "The volume of monsters", null, (VoicechatServerApi) RecordingSimpleVoiceChat.vcApi);
    }

    @SubscribeEvent
    public void onSpeakingEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof SpeakingEntity) {
            entity.dropAllDeathLoot(Objects.requireNonNull(event.getSource()));
            event.setCanceled(true); // Prevent default death behavior
            entity.remove(Entity.RemovalReason.KILLED); // Disappear instantly
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event){
        if(!event.getEntity().level().isClientSide() && event.getEntity() instanceof Player victim){
            DamageSource source = event.getSource();
            RevervoxMod.LOGGER.debug("damage source: {}", source);
            if(source == null){
                return;
            }
            RevervoxMod.LOGGER.debug("attacker entity: {}", source.getEntity());
            if(source.getEntity() == null){
                RevervoxMod.LOGGER.debug("no entity source");
                return;
            }
            if(source.getEntity() instanceof Player attacker && RecordingSimpleVoiceChat.vcApi instanceof VoicechatServerApi api){
                RevervoxMod.LOGGER.debug("is player && serverapi");
                if(attacker.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IRevervoxWeapon){
                    short[] audio = RecordingSimpleVoiceChatPlugin.getRandomAudio(victim.getUUID(), false);
                    if(audio == null) { return; }
                    AudioChannel channel = api.createLocationalAudioChannel(
                            UUID.randomUUID(),
                            api.fromServerLevel(victim.level()),
                            api.createPosition(victim.getX(), victim.getY(), victim.getZ()));
                    if(channel != null){
                        channel.setCategory(RevervoxMod.MOD_ID);
                        new AudioPlayer(audio, api, channel).start();
                    }
                }
            }
        }
    }
}
