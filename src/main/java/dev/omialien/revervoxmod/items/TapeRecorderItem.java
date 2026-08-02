package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechatrecording.api.IRecordedPlayer;
import dev.omialien.voicechatrecording.voicechat.RecordedPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class TapeRecorderItem extends Item {
    public static Map<ServerPlayer, Long> PLAYERS_FINISHED_USING = new WeakHashMap<>();

    public TapeRecorderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        UUID player = pPlayer.getUUID();
        IRecordedPlayer recPlayer = RevervoxMod.RECORDING_API.getRecordedPlayer(player);
        if (!pLevel.isClientSide && recPlayer != null) {
            ((RecordedPlayer)recPlayer).saveCurrentRecording();
        }
        pPlayer.startUsingItem(pUsedHand);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 20*20;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
        if (entity instanceof ServerPlayer player) {
            IRecordedPlayer recPlayer = RevervoxMod.RECORDING_API.getRecordedPlayer(player.getUUID());
            if (recPlayer != null) {
                // TODO add stopRecording to public API
                ((RecordedPlayer)recPlayer).saveCurrentRecording();
                PLAYERS_FINISHED_USING.put(player, player.level().getGameTime());
            }
        }
    }

    public static boolean hasStoppedUsing(ServerPlayer sPlayer) {
        if (PLAYERS_FINISHED_USING.containsKey(sPlayer)) {
            Long time = PLAYERS_FINISHED_USING.get(sPlayer);
            return time != null && time + 20 >= sPlayer.level().getGameTime();
        }
        return false;
    }
}
