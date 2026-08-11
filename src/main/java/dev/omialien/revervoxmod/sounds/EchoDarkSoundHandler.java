package dev.omialien.revervoxmod.sounds;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.worldgen.biome.RevervoxBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, value = Dist.CLIENT)
public class EchoDarkSoundHandler {
    private static EchoDarkAmbience echoDarkAmbienceSound = null;
    private static EchoDarkHeartbeat echoDarkHeartbeatSound = null;
    private static SoundManager soundManager = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        if (player.tickCount % 20 != 0) return;

        Holder<Biome> biome = mc.level.getBiome(player.blockPosition());
        boolean inTargetBiome = biome.is(RevervoxBiomes.REVERVOX_BIOME);
        boolean inDarkness = player.level().getBrightness(LightLayer.BLOCK, player.blockPosition()) <= 11;

        soundManager = mc.getSoundManager();

        if (inTargetBiome && echoDarkAmbienceSound == null) {
            echoDarkAmbienceSound = new EchoDarkAmbience(player);
            soundManager.play(echoDarkAmbienceSound);
        } else if (!inTargetBiome && echoDarkAmbienceSound != null) {
            echoDarkAmbienceSound.startFadeOut();
            echoDarkAmbienceSound = null;
        }

        if (inTargetBiome && inDarkness && echoDarkHeartbeatSound == null){
            echoDarkHeartbeatSound = new EchoDarkHeartbeat(player);
            soundManager.play(echoDarkHeartbeatSound);
        } else if (!inDarkness && echoDarkHeartbeatSound != null) {
            echoDarkHeartbeatSound.startPitchFadeOut();
            echoDarkHeartbeatSound = null;
        }
    }

    public static void resetHeartbeat(){
        soundManager.stop(echoDarkHeartbeatSound);
        echoDarkHeartbeatSound = null;
    }
}
