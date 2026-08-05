package dev.omialien.revervoxmod.sounds;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.worldgen.biome.RevervoxBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, value = Dist.CLIENT)
public class EchoDarkAmbientHandler {
    private static EchoDarkAmbience currentSound = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        if (player.tickCount % 20 != 0) return;

        Holder<Biome> biome = mc.level.getBiome(player.blockPosition());
        boolean inTargetBiome = biome.is(RevervoxBiomes.REVERVOX_BIOME);

        SoundManager soundManager = mc.getSoundManager();

        if (inTargetBiome && currentSound == null) {
            currentSound = new EchoDarkAmbience(player);
            soundManager.play(currentSound);
        } else if (!inTargetBiome && currentSound != null) {
            currentSound.startFadeOut();
            currentSound = null;
        }
    }
}
