package dev.omialien.revervoxmod;

import com.mojang.logging.LogUtils;
import de.maxhenkel.voicechat.api.VoicechatApi;
import dev.omialien.revervoxmod.config.RevervoxModClientConfigs;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxFakeBatEntity;
import dev.omialien.revervoxmod.events.ClientForgeEventBus;
import dev.omialien.revervoxmod.events.CommonForgeEventBus;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.registries.*;
import dev.omialien.revervoxmod.taskscheduler.TaskScheduler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RevervoxMod.MOD_ID)
public class RevervoxMod {
    public static final String MOD_ID = "revervox_mod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final LevelResource AUDIO_DIRECTORY = new LevelResource("player_audios");
    public static VoicechatApi vcApi = null;
    public static TaskScheduler TASKS = new TaskScheduler();

    public RevervoxMod(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.register(new ClientForgeEventBus()));
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

    public static void summonBatWave(Player player){
        if(!player.level().isClientSide()){
            Vec3 playerPos = player.getPosition(0);
            for(int i = 0; i <= 2; i++){
                for(int j = -1; j <= 1; j++){
                    RevervoxFakeBatEntity ent = new RevervoxFakeBatEntity(EntityRegistry.REVERVOX_FAKE_BAT.get(), player.level());
                    Vec3 batpos = RevervoxMod.applyLocalCoordinates(player.getYRot(), playerPos, 10, i, j);
                    float rotat = player.getYRot() + 180;
                    ent.setYBodyRot(rotat);
                    ent.setYHeadRot(rotat);
                    ent.moveTo(batpos.x, batpos.y, batpos.z, rotat, 0);
                    ent.setRotation(rotat);
                    ent.setTarget(player);
                    player.level().addFreshEntity(ent);
                }
            }
        }
    }

    public static Vec3 applyLocalCoordinates(float yRot, Vec3 vec3, float forwards, float up, float left){
        Vec2 vec2 = new Vec2(0,yRot);
        float f = Mth.cos((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        float f1 = Mth.sin((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(-vec2.x * ((float)Math.PI / 180F));
        float f3 = Mth.sin(-vec2.x * ((float)Math.PI / 180F));
        float f4 = Mth.cos((-vec2.x + 90.0F) * ((float)Math.PI / 180F));
        float f5 = Mth.sin((-vec2.x + 90.0F) * ((float)Math.PI / 180F));
        Vec3 vec31 = new Vec3((f * f2), f3, (f1 * f2));
        Vec3 vec32 = new Vec3((f * f4), f5, (f1 * f4));
        Vec3 vec33 = vec31.cross(vec32).scale(-1.0D);
        double d0 = vec31.x * forwards + vec32.x * up + vec33.x * left;
        double d1 = vec31.y * forwards + vec32.y * up + vec33.y * left;
        double d2 = vec31.z * forwards + vec32.z * up + vec33.z * left;
        return new Vec3(vec3.x + d0, vec3.y + d1, vec3.z + d2);
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
}
