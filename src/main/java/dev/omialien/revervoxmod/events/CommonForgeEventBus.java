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
import dev.omialien.revervoxmod.voicechat.RecordedPlayer;
import dev.omialien.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import dev.omialien.revervoxmod.voicechat.audio.AudioPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.nio.file.Files;
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
    public void mobSpawnEvent(MobSpawnEvent.FinalizeSpawn event){
        if (event.getEntity() instanceof Bat && !event.getLevel().isClientSide()) {
            if (new Random().nextInt(RevervoxModServerConfigs.REVERVOX_BAT_SPAWN_CHANCE.get()) == 0){
                RevervoxBatGeoEntity bat = new RevervoxBatGeoEntity(EntityRegistry.REVERVOX_BAT.get(), event.getLevel().getLevel());
                bat.moveTo(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
                RevervoxMod.LOGGER.info("Spawning Revervox Bat! at " + event.getEntity().getX() + ", " + event.getEntity().getY() + ", " + event.getEntity().getZ());
                event.setSpawnCancelled(true);
                event.getLevel().addFreshEntity(bat);
            }
        }
    }


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        NearestEntityPlayVoiceCommand.register(event.getDispatcher());
        StartRecordingCommand.register(event.getDispatcher());
        StopRecordingCommand.register(event.getDispatcher());
        isRecordingCommand.register(event.getDispatcher());
        ScheduleLogCommand.register(event.getDispatcher());

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
            if(source.getEntity() instanceof Player attacker && RevervoxMod.vcApi instanceof VoicechatServerApi api){
                RevervoxMod.LOGGER.debug("is player && serverapi");
                if(attacker.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IRevervoxWeapon){
                    short[] audio = RevervoxVoicechatPlugin.getRandomAudio(victim.getUUID(), false);
                    if(audio == null) { return; }
                    AudioChannel channel = api.createLocationalAudioChannel(
                            UUID.randomUUID(),
                            api.fromServerLevel(victim.level()),
                            api.createPosition(victim.getX(), victim.getY(), victim.getZ()));
                    new AudioPlayer(audio, api, channel).start();
                }
            }
        }
    }
}
