package dev.omialien.revervoxmod;

import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxFakeBatEntity;
import dev.omialien.revervoxmod.registries.*;
import dev.omialien.voicechat_recording.taskscheduler.TaskScheduler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(RevervoxMod.MOD_ID)
public class RevervoxMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "revervox_mod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final TaskScheduler TASKS = new TaskScheduler();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public RevervoxMod(IEventBus modEventBus, ModContainer modContainer) {
        EntityRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        CreativeTabRegistry.register(modEventBus);
        ParticleRegistry.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, RevervoxModServerConfigs.SPEC);
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
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 27, 100, false, false), player);
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
}
