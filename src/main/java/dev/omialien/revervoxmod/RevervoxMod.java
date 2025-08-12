package dev.omialien.revervoxmod;

import com.mojang.logging.LogUtils;
import de.maxhenkel.voicechat.api.VoicechatApi;
import dev.omialien.revervoxmod.commands.*;
import dev.omialien.revervoxmod.config.RevervoxModClientConfigs;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.SpeakingEntity;
import dev.omialien.revervoxmod.events.ClientForgeEventBus;
import dev.omialien.revervoxmod.events.CommonForgeEventBus;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.registries.*;
import dev.omialien.revervoxmod.taskscheduler.TaskScheduler;
import dev.omialien.revervoxmod.voicechat.RecordedPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Mod(RevervoxMod.MOD_ID)
public class RevervoxMod {
    public static final String MOD_ID = "revervox_mod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final LevelResource AUDIOS = new LevelResource("player_audios");
    public static VoicechatApi vcApi = null;
    public static TaskScheduler TASKS = new TaskScheduler();

    public RevervoxMod(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(new ClientForgeEventBus());
        });
        MinecraftForge.EVENT_BUS.register(new CommonForgeEventBus());

        EntityRegistry.register(context.getModEventBus());
        SoundRegistry.register(context.getModEventBus());
        ItemRegistry.register(context.getModEventBus());
        CreativeTabRegistry.register(context.getModEventBus());
        ParticleRegistry.register(context.getModEventBus());

        RevervoxPacketHandler.registerPackets();

        context.registerConfig(ModConfig.Type.CLIENT, RevervoxModClientConfigs.SPEC, "revervox-client.toml");
        context.registerConfig(ModConfig.Type.SERVER, RevervoxModServerConfigs.SPEC, "revervox-server.toml");
    }

    private void setup(FMLCommonSetupEvent event) {
        LOGGER.debug("Setting up Revervox Mod");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.debug("Server starting");
        RecordedPlayer.audiosPath = event.getServer().getWorldPath(AUDIOS);
        if(!Files.exists(RecordedPlayer.audiosPath)){
            try {
                Files.createDirectory(RecordedPlayer.audiosPath);
            } catch (IOException e) {
                LOGGER.error("Error creating audios directory: " + e.getMessage());
            }
        }
    }


    //TODO salvar audios quando server crasha, rn o onPlayerDisconnected é chamado mas n vai a tempo de salvar os audios
    /*
    @SubscribeEvent
    public void onServerCrash(ServerStoppedEvent event) {
            RevervoxMod.LOGGER.debug("Saving audios from player");
            if (RevervoxVoicechatPlugin.ran) {
                RevervoxMod.LOGGER.debug("ran is true");
                return;
            }
            for (UUID uuid : RevervoxVoicechatPlugin.getRecordedPlayers().keySet()){
                RevervoxVoicechatPlugin.getRecordedPlayer(uuid).saveAudios();
            }
    }

     */


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        NearestEntityPlayVoiceCommand.register(event.getDispatcher());
        StartRecordingCommand.register(event.getDispatcher());
        StopRecordingCommand.register(event.getDispatcher());
        isRecordingCommand.register(event.getDispatcher());
        ScheduleLogCommand.register(event.getDispatcher());

    }

    @SubscribeEvent
    public void onSpeakingEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof SpeakingEntity) {
            entity.dropAllDeathLoot(Objects.requireNonNull(event.getSource()));
            event.setCanceled(true); // Prevent default death behavior
            entity.remove(Entity.RemovalReason.KILLED); // Dissapear instantly
        }
    }
}
