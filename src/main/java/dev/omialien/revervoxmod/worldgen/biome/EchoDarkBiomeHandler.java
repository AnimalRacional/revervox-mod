package dev.omialien.revervoxmod.worldgen.biome;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechatrecording.api.AudioEffect;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class EchoDarkBiomeHandler {
    private static int currentAudioDelay = 3;
    private final static int AUDIO_DELAY = 3; // in seconds


    // can only be called on the server
    public static void tickBiomeLogic(Player player){
        if (player.tickCount % 20 != 0) return;

        player.addEffect(new MobEffectInstance(
                MobEffects.DARKNESS,
                80,
                1,
                false,
                false,
                false
        ));

        if (currentAudioDelay > 0) {
            currentAudioDelay--;
            return;
        }
        // TODO quantos mais players mais spam por isso prevenir isso;
        // fazer aparecer em sitios diferentes o audio
        // vai ficando mais intenso (mais vozes)
        // dar trigger ao bat aparecer no ecrã quando muito tempo no escuro

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel serverLevel = serverPlayer.serverLevel();
        int blockLight = serverLevel.getBrightness(LightLayer.BLOCK, serverPlayer.blockPosition());

        if (blockLight > 11) return;
        Vec3 audioPos = serverPlayer.position();
        IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(false);
        if(audio != null){
            RevervoxMod.LOGGER.debug("playing EchoDarkBiome audio");
            AudioPlayingUtil.playLocationalAudio(
                    audio, audioPos, serverLevel,
                    AudioEffect.random(),
                    RevervoxMod.MOD_ID, 32.0f
            );
            currentAudioDelay = AUDIO_DELAY;
        }
    }
}
