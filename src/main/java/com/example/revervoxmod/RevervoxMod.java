package com.example.revervoxmod;

import com.example.revervoxmod.commands.NearestEntityPlayVoiceCommand;
import com.example.revervoxmod.commands.StartRecordingCommand;
import com.example.revervoxmod.commands.StopRecordingCommand;
import com.example.revervoxmod.commands.isRecordingCommand;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.registries.CreativeTabRegistry;
import com.example.revervoxmod.registries.EntityRegistry;
import com.example.revervoxmod.registries.ItemRegistry;
import com.example.revervoxmod.registries.SoundRegistry;
import com.example.revervoxmod.voicechat.RecordedPlayer;
import com.mojang.logging.LogUtils;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

@Mod(RevervoxMod.MOD_ID)
public class RevervoxMod {
    public static final String MOD_ID = "revervox_mod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final LevelResource AUDIOS = new LevelResource("player_audios");
    public static VoicechatApi vcApi = null;


    public RevervoxMod(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.register(context.getModEventBus());
        SoundRegistry.register(context.getModEventBus());
        ItemRegistry.register(context.getModEventBus());
        CreativeTabRegistry.register(context.getModEventBus());
    }

    private void setup(FMLCommonSetupEvent event) {
        LOGGER.info("Setting up Revervox Mod");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting");
        RecordedPlayer.audiosPath = event.getServer().getWorldPath(AUDIOS);
        if(!Files.exists(RecordedPlayer.audiosPath)){
            try {
                Files.createDirectory(RecordedPlayer.audiosPath);
            } catch (IOException e) {
                LOGGER.error("Error creating audios directory: " + e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        NearestEntityPlayVoiceCommand.register(event.getDispatcher());
        StartRecordingCommand.register(event.getDispatcher());
        StopRecordingCommand.register(event.getDispatcher());
        isRecordingCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent e){
        if(e.getEntity() instanceof Player player){
            if(e.getSource().getEntity() instanceof RevervoxGeoEntity revervox){
                if(player.level().isClientSide()){
                    // TODO isto não acontece, partículas só funcionam client-side
                    // então é preciso descobrir ou porque não acontece ou outra forma de detetar kills
                    revervox.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
                } else if(RevervoxMod.vcApi instanceof VoicechatServerApi api){
                    revervox.disappear(player, api);
                }
            }
        }
    }

}
