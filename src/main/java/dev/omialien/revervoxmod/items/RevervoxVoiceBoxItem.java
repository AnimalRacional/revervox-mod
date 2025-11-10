package dev.omialien.revervoxmod.items;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.voicechat.AudioStorage;
import dev.omialien.voicechatrecording.VoiceChatRecording;
import dev.omialien.voicechatrecording.voicechat.audio.AudioPlayer;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RevervoxVoiceBoxItem extends Item {
    private int audioDuration;
    private AudioChannel audioChannel;
    private Player playingPlayer;
    private AudioChannel getChannel(Player plr){
        if ((audioChannel == null || !plr.is(playingPlayer)) && VoiceChatRecording.vcApi instanceof VoicechatServerApi api){
            playingPlayer = plr;
            audioChannel = api.createEntityAudioChannel(UUID.randomUUID(), api.fromEntity(plr));
        }
        return audioChannel;
    }

    public RevervoxVoiceBoxItem(Properties pProperties) {
        super(pProperties);
        audioDuration = 0;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        /* TODO apparently only one instance of each Item exists, ItemStack is what has information for each stack of that item
             so we shouldn't store audioDuration, audioChannel or playingPlayer in the Item, but the ItemStack
             this is done through nbt in 1.20.1 and data components in 1.21.1, so to prevent too much annoyance
             review if all those variables are needed
             for example, I don't think we need to store a channel by player, but only by ItemStack
             https://forums.minecraftforge.net/topic/83388-adding-nbt-to-item/
        */
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        boolean audioPlayed = false;
        if (!pLevel.isClientSide() && VoiceChatRecording.vcApi instanceof VoicechatServerApi api) {
            IRecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(false);
            if(audio != null){
                this.audioDuration =  audio.getAudio().length / AudioStorage.SAMPLE_RATE;
                playAudio(pPlayer, api, audio.getAudio());
                audioPlayed = true;
                pPlayer.startUsingItem(pUsedHand);
            } else {this.audioDuration = 1; }
            int cd = (int)Math.round((this.audioDuration + 0.5) * 20);
            pPlayer.getCooldowns().addCooldown(this, cd);
            this.audioDuration = cd;
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return audioPlayed ? InteractionResultHolder.pass(item) : InteractionResultHolder.fail(item);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return (this.audioDuration + 1) * 20;
    }

    private void playAudio(Player pPlayer, VoicechatServerApi api, short[] audio){
        if(audio != null){
            if(getChannel(pPlayer) != null) {
                new AudioPlayer(audio, api, getChannel(pPlayer)).start();
            }
        }
    }
}
