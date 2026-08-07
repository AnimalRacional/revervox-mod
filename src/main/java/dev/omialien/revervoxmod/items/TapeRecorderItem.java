package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.client.TapeRecorderItemExtensions;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.voicechatrecording.api.AudioId;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.IRecordedPlayer;
import net.minecraft.nbt.CompoundTag;
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

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class TapeRecorderItem extends Item implements GeoItem {
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
            recPlayer.forceFinishRecording();
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
                Optional<IRecordedAudio> audioOpt = recPlayer.forceFinishRecording();
                if (audioOpt.isEmpty()) { return; }
                IRecordedAudio audio = audioOpt.get();
                if (audio.getDuration() < 0.05) { return; }
                TapeItem.record(AudioId.of(audio.getPlayerUUID(), audio.getId()), stack, player);
                if (!player.getAbilities().instabuild) {
                    CompoundTag tag = stack.getTag();
                    ItemStack newStack = new ItemStack(ItemRegistry.TAPE_RECORDER_OFF.get());
                    if (tag != null) {
                        tag.remove(TapeItem.TAPE_COMPONENTS);
                        newStack.setTag(tag);
                    }
                    player.setItemInHand(player.getUsedItemHand(), newStack);
                }
                audio.saveAudio(RevervoxMod.BLOCK_NAMESPACE);
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
