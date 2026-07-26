package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.voicechat.AudioStorage;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RevervoxVoiceBoxItem extends Item {

    public RevervoxVoiceBoxItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        boolean audioPlayed = false;
        if (!pLevel.isClientSide()) {
            IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(false);
            int audioDuration;
            if(audio != null){
                audioDuration =  audio.getAudio().length / AudioStorage.SAMPLE_RATE;
                AudioPlayingUtil.playFromEntity(audio, pPlayer, RevervoxMod.MOD_ID);
                audioPlayed = true;
                pPlayer.startUsingItem(pUsedHand);
            } else { audioDuration = 1; }
            int cd = (int)Math.round((audioDuration + 0.5) * 20);
            pPlayer.getCooldowns().addCooldown(this, cd);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return audioPlayed ? InteractionResultHolder.pass(item) : InteractionResultHolder.fail(item);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 30;
    }
}
