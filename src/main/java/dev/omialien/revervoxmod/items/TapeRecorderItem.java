package dev.omialien.revervoxmod.items;

import dev.omialien.voicechatrecording.AudioId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TapeRecorderItem extends Item {
    public final static String PLAYER_ID = "audio_player_id";
    public final static String AUDIO_ID = "audio_id";

    public TapeRecorderItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        if (super.isFoil(pStack)) {
            return true;
        }
        CompoundTag tag = pStack.getTag();
        return tag != null && tag.contains(PLAYER_ID);
    }

    public static boolean hasRecording(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(PLAYER_ID);
    }

    public static void record(AudioId audio, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putUUID(PLAYER_ID, audio.player());
        tag.putUUID(AUDIO_ID, audio.audio());
    }

    public static AudioId getAudio(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? null : tag.contains(PLAYER_ID) ?
                AudioId.of(tag.getUUID(PLAYER_ID), tag.getUUID(AUDIO_ID)) : null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        AudioId id = getAudio(pStack);
        if (id != null) {
            Component playerName = null;
            if (pLevel != null) {
                Player p = pLevel.getPlayerByUUID(id.player());
                if (p != null) {
                    playerName = p.getDisplayName();
                }
            }
            if (playerName == null) {
                String username = UsernameCache.getLastKnownUsername(id.player());
                if (username == null) {
                    username = id.player().toString();
                }
                playerName = Component.literal(username);
            }
            pTooltipComponents.add(Component.literal("Audio recorded by ").append(playerName));
        }
    }
}
