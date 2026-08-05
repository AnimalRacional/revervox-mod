package dev.omialien.revervoxmod.worldgen.biome;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.client.RevervoxBatRenderHelper;
import dev.omialien.voicechatrecording.api.AudioEffect;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public class EchoDarkBiomeHandler {
    private static int currentAudioDelay = 3;
    private final static int AUDIO_DELAY = 3; // in seconds
    static Map<Player, Integer> PLAYER_TICKS_IN_DARKNESS = new WeakHashMap<>();

    //TODO CORRE NO CLIENTE N SEI COMO FAZER NO SERVER E DPS DAR DANO
    public static void tickBiomeLogicClient(Player player){
        PLAYER_TICKS_IN_DARKNESS.put(player, PLAYER_TICKS_IN_DARKNESS.getOrDefault(player, 0) + 1);

        if (PLAYER_TICKS_IN_DARKNESS.get(player) > 200) {
            if (player.level().isClientSide && player instanceof AbstractClientPlayer acp) {
                RevervoxMod.LOGGER.info("activating RevervoxBatRenderHelper on client");
                RevervoxBatRenderHelper.activate(acp, 60);
            }
            PLAYER_TICKS_IN_DARKNESS.remove(player);
            return;
        }

        int playerBlockLight = player.level().getBrightness(LightLayer.BLOCK, player.blockPosition());
        if (playerBlockLight > 11) PLAYER_TICKS_IN_DARKNESS.remove(player);
    }

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
        // vai ficando mais intenso (mais vozes)
        // dar trigger ao bat aparecer no ecrã quando muito tempo no escuro

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel serverLevel = serverPlayer.serverLevel();
        int blockLight = serverLevel.getBrightness(LightLayer.BLOCK, serverPlayer.blockPosition());

        if (blockLight > 11) return;
        Vec3 playerPos = serverPlayer.position();
        RandomSource random = serverLevel.random;
        Vec3 audioPos = randomPosInCircle(playerPos, 4, 20, random);
        IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(false);
        if(audio != null){
            RevervoxMod.LOGGER.debug("playing EchoDarkBiome audio");
            AudioPlayingUtil.playLocationalAudio(
                    audio, audioPos, serverLevel,
                    //AudioEffect.reverse().makeEcho(0.4f, 300, 4),
                    //AudioEffect.reverse().makeGlitch(0.6f, 0.1f, 4).makeEcho(0.4f, 300, 4),
                    //AudioEffect.stutter(0.1f, 5),
                    AudioEffect.reverse().addRandomEffects().exclude(AudioEffect.Effect.REVERB).exclude(AudioEffect.Effect.ROBOT).exclude(AudioEffect.Effect.MULTI_PITCH),
                    RevervoxMod.MOD_ID, 32.0f
            );
            currentAudioDelay = AUDIO_DELAY;
        }
    }

    public static Vec3 randomPosInCircle(Vec3 center, double minRadiusSq, double radius, RandomSource random) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double r = Math.sqrt(minRadiusSq + random.nextDouble() * (radius * radius - minRadiusSq));

        double x = center.x + r * Math.cos(angle);
        double z = center.z + r * Math.sin(angle);

        return new Vec3(x, center.y, z);
    }
}
