package dev.omialien.revervoxmod.blocks;

import dev.omialien.revervoxmod.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;


public class NightmareChestBlockEntity extends ChestBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Boolean nightmare = null;
    private final int NIGHTMARE_CHANCE = 4; //TODO config
    private static final RawAnimation CLOSED = RawAnimation.begin().thenPlayAndHold("misc.close");
    private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("misc.open");
    private static final RawAnimation NIGHTMARE_OPEN = RawAnimation.begin().then("misc.nightmare_open", Animation.LoopType.PLAY_ONCE);


    public NightmareChestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.NIGHTMARE_CHEST.get(), pPos, pBlockState);
    }

    public boolean isNightmare() {
        if (this.nightmare == null && this.level != null && !this.level.isClientSide) {
            this.nightmare = this.level.getRandom().nextInt(NIGHTMARE_CHANCE) == 0;
            setChanged();
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        return this.nightmare != null && this.nightmare;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.nightmare != null) tag.putBoolean("Nightmare", this.nightmare);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.nightmare = tag.contains("Nightmare") ? tag.getBoolean("Nightmare") : null;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "NightmareChest", 0, state -> {
            if (getOpenNess(state.getPartialTick()) > 0.01f)
                return state.setAndContinue(isNightmare() ? NIGHTMARE_OPEN : OPEN);
            return state.setAndContinue(CLOSED);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }
}
