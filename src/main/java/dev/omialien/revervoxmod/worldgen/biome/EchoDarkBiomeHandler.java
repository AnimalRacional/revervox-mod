package dev.omialien.revervoxmod.worldgen.biome;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

public class EchoDarkBiomeHandler {
    private static int audioDelay = 3;


    //can only be called on the server
    public static void tickBiomeLogic(Player player){
        if (player.tickCount % 20 != 0) return;

        Level level = player.level();
        BlockPos pos = player.blockPosition();
        Holder<Biome> biome = level.getBiome(pos);

        boolean inTargetBiome = biome.is(RevervoxBiomes.REVERVOX_BIOME);

        if (inTargetBiome) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.DARKNESS,
                    80,
                    1,
                    false,
                    false,
                    false
            ));
        }

        if (audioDelay > 0) {
            audioDelay--;
            return;
        }
        // TODO quantos mais players mais spam por isso prevenir isso;
        // fazer aparecer em sitios diferentes o audio
        // adicionar sound effects
        ServerPlayer serverPlayer = (ServerPlayer) player;
        Vec3 audioPos = serverPlayer.position();
        IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(false);
        if(audio != null){
            RevervoxMod.LOGGER.debug("playing EchoDarkBiome audio");
            AudioPlayingUtil.playLocationalAudio(audio, audioPos, serverPlayer.serverLevel(), RevervoxMod.MOD_ID);
            audioDelay = 3;
        }
    }
}
