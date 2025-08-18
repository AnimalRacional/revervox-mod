package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechat_recording.RecordingSimpleVoiceChat;
import dev.omialien.voicechat_recording.VoiceChatRecording;
import dev.omialien.voicechat_recording.voicechat.RecordingSimpleVoiceChatPlugin;
import dev.omialien.voicechat_recording.voicechat.VoiceChatRecordingPlugin;
import dev.omialien.voicechat_recording.voicechat.audio.AudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AudioRepeatingItem extends Item {
    private int audioDuration;

    public AudioRepeatingItem(Properties pProperties) {
        super(pProperties);
        audioDuration = 0;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        boolean audioPlayed = false;
        if (!pLevel.isClientSide() && VoiceChatRecording.vcApi instanceof VoicechatServerApi api) {
            short[] audio = VoiceChatRecordingPlugin.getRandomAudio(false);
            if(audio != null){
                this.audioDuration =  audio.length / VoiceChatRecordingPlugin.SAMPLE_RATE;
                playAudio(pPlayer, api, audio);
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
    public int getUseDuration(@NotNull ItemStack pStack) {
        return (this.audioDuration + 1) * 20;
    }

    private void playAudio(Player pPlayer, VoicechatServerApi api, short[] audio){
        AudioChannel channel = api.createEntityAudioChannel(UUID.randomUUID(), api.fromEntity(pPlayer));
        if(channel != null){
            channel.setCategory(RevervoxMod.MOD_ID);
            if(audio != null){
                new AudioPlayer(audio, api, channel).start();
            }
        }
    }
}
