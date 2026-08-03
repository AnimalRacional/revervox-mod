package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.client.TapeRecorderItemExtensions;
import dev.omialien.voicechatrecording.api.IRecordedPlayer;
import dev.omialien.voicechatrecording.voicechat.RecordedPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class TapeRecorderItem extends Item implements GeoItem {
    public static Map<ServerPlayer, Long> PLAYERS_FINISHED_USING = new WeakHashMap<>();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TapeRecorderItem(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
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

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    //TODO: adicionado para substituir o evento de registrar itemExtensions
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new TapeRecorderItemExtensions(true));
    }

    public static boolean hasStoppedUsing(ServerPlayer sPlayer) {
        if (PLAYERS_FINISHED_USING.containsKey(sPlayer)) {
            Long time = PLAYERS_FINISHED_USING.get(sPlayer);
            return time != null && time + 20 >= sPlayer.level().getGameTime();
        }
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
