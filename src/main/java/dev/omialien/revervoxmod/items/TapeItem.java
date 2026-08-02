package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.voicechatrecording.AudioId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TapeItem extends Item {
    public final static String PLAYER_ID = "audio_player_id";
    public final static String AUDIO_ID = "audio_id";
    public final static String TAPE_COMPONENTS = "tape_components";

    public TapeItem(Properties pProperties) {
        super(pProperties);
    }

    public static boolean hasRecording(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(PLAYER_ID);
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack pStack) {
        CompoundTag tag = pStack.getTag();
        if (tag != null && tag.contains(PLAYER_ID)) {
            return super.getDescriptionId(pStack) + "_recorded";
        } else {
            return super.getDescriptionId(pStack);
        }
    }

    public static void setAudio(AudioId audio, CompoundTag tag) {
        tag.putUUID(PLAYER_ID, audio.player());
        tag.putUUID(AUDIO_ID, audio.audio());
    }

    public static void record(AudioId audio, ItemStack stack, ServerPlayer player) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAPE_COMPONENTS)) {
            tag = tag.getCompound(TAPE_COMPONENTS).copy();
        } else {
            tag = new CompoundTag();
        }
        setAudio(audio, tag);
        ItemStack newStack = new ItemStack(ItemRegistry.TAPE.get(), 1);
        newStack.setTag(tag);
        player.drop(newStack, true, true);
    }

    public static AudioId getAudio(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? null : tag.contains(PLAYER_ID) ?
                AudioId.of(tag.getUUID(PLAYER_ID), tag.getUUID(AUDIO_ID)) : null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
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
