package dev.omialien.revervoxmod.blocks;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.TapeRecorderItem;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.voicechatrecording.AudioId;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class VoiceRepeaterBlock extends BaseEntityBlock {
    public static final BooleanProperty CURRENTLY_PLAYING = BooleanProperty.create("voice_repeater_playing");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public VoiceRepeaterBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CURRENTLY_PLAYING, false));
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        setPlaying(false, pState, pLevel, pPos);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        setPlaying(false, pState, pLevel, pPos);
    }

    private static boolean isPlaying(BlockState pState) {
        return pState.getValue(CURRENTLY_PLAYING);
    }

    private void setPlaying(boolean playing, BlockState pState, ServerLevel pLevel, BlockPos pPos) {
        pLevel.setBlock(pPos, pState.setValue(CURRENTLY_PLAYING, playing), UPDATE_ALL);
    }

    @Override
    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        super.attack(pState, pLevel, pPos, pPlayer);
        if (pLevel instanceof ServerLevel level) {
            if (!VoiceRepeaterBlock.isPlaying(pState)) {
                this.play(pState, level, pPos);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        InteractionResult interact = super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        if (isPlaying(pState)) {
            return interact;
        }
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.getItem() == ItemRegistry.TAPE_RECORDER.get() && TapeRecorderItem.hasRecording(stack)) {
            if (pLevel instanceof ServerLevel level && !interact.consumesAction()) {
                AudioId id = TapeRecorderItem.getAudio(stack);
                if (level.getBlockEntity(pPos) instanceof VoiceRepeaterBlockEntity be) {
                    be.setAudio(id);
                    pLevel.updateNeighborsAt(pPos, pState.getBlock());
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            if (pLevel instanceof ServerLevel level && level.getBlockEntity(pPos) instanceof VoiceRepeaterBlockEntity be) {
                be.popOutTape();
                pLevel.updateNeighborsAt(pPos, pState.getBlock());
            }
        }
        return InteractionResult.PASS;
    }

    private void play(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
        if (isPlaying(pState)) {
            return;
        }
        if (pLevel.getBlockEntity(pPos) instanceof VoiceRepeaterBlockEntity be) {
            AudioId audioId = be.getAudio();
            if (audioId != null) {
                try {
                    RevervoxMod.LOGGER.debug("Playing audio {} {}", audioId.player(), audioId.audio());
                    IRecordedAudio audio = RevervoxMod.RECORDING_API.loadAudio(audioId.player(), audioId.audio()).get();
                    AudioPlayingUtil.playLocationalAudio(audio, pPos.getCenter(), pLevel, RevervoxMod.MOD_ID);
                    pLevel.scheduleTick(pPos, pState.getBlock(), ((int)audio.getDuration())*20 + 20);
                    setPlaying(true, pState, pLevel, pPos);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        boolean powered = pLevel.hasNeighborSignal(pPos);
        if (powered != pState.getValue(POWERED)) {
            pLevel.setBlock(pPos, pState.setValue(POWERED, powered), UPDATE_ALL);
            if (powered && pLevel instanceof ServerLevel level) {
                this.play(pState, level, pPos);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(CURRENTLY_PLAYING, POWERED);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        RevervoxMod.LOGGER.debug("CALLED NEW BLOCK ENTITY");
        return new VoiceRepeaterBlockEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            if(pLevel.getBlockEntity(pPos) instanceof VoiceRepeaterBlockEntity be) {
                be.popOutTape();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof VoiceRepeaterBlockEntity be) {
            AudioId id = be.getAudio();
            try {
                IRecordedAudio audio = RevervoxMod.RECORDING_API.loadAudio(id.player(), id.audio()).get();
                return Math.max(Mth.floor((audio.getDuration() / 22.0) * 16), 15);
            } catch (Exception e) {
                return 0;
            }

        }
        return super.getAnalogOutputSignal(pState, pLevel, pPos);
    }
}
