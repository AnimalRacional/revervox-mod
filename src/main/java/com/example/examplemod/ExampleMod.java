package com.example.examplemod;

import com.example.examplemod.commands.NearestEntityPlayVoiceCommand;
import com.example.examplemod.commands.StartRecordingCommand;
import com.example.examplemod.commands.StopRecordingCommand;
import com.example.examplemod.commands.isRecordingCommand;
import com.example.examplemod.registries.CreativeTabRegistry;
import com.example.examplemod.registries.EntityRegistry;
import com.example.examplemod.registries.ItemRegistry;
import com.example.examplemod.registries.SoundRegistry;
import com.example.examplemod.voicechat.RecordedPlayer;
import com.mojang.logging.LogUtils;
import de.maxhenkel.voicechat.api.VoicechatApi;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

@Mod(ExampleMod.MOD_ID)
public class ExampleMod {
    public static final String MOD_ID = "example_mod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final LevelResource AUDIOS = new LevelResource("player_audios");
    public static VoicechatApi vcApi = null;


    public ExampleMod(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.register(context.getModEventBus());
        SoundRegistry.register(context.getModEventBus());
        ItemRegistry.register(context.getModEventBus());
        CreativeTabRegistry.register(context.getModEventBus());
    }

    private void setup(FMLCommonSetupEvent event) {
        LOGGER.info("Setting up example mod");
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

}
