package dev.omialien.revervoxmod.blocks;

import dev.omialien.revervoxmod.registries.BlockEntityRegistry;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.voicechatrecording.AudioId;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class VoiceRepeaterBlockEntity extends BlockEntity {
    private AudioId audio;
    private CompoundTag itemTags;
    private final static String PLAYER_ID = "audio_player_id";
    private final static String AUDIO_ID = "audio_id";
    private final static String TAPE_TAGS = "tape_tags";
    public VoiceRepeaterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.TAPEBOX.get(), pPos, pBlockState);
    }

    public AudioId getAudio() {
        return this.audio;
    }

    public void setAudio(AudioId audioId, CompoundTag tags) {
        if (this.level == null) { return; }
        if (this.audio != null) {
            popOutTape();
        }
        this.itemTags = tags;
        this.audio = audioId;
    }

    public void popOutTape() {
        if (this.level == null) {
            return;
        }
        if (this.audio == null) {
            return;
        }
        ItemStack stack = new ItemStack(ItemRegistry.TAPE.get());
        stack.setTag(itemTags);
        Vec3 pos = Vec3.atLowerCornerWithOffset(this.getBlockPos(), 0.5D, 1.01D, 0.5D)
                .offsetRandom(this.level.random, 0.7F);
        ItemEntity entity = new ItemEntity(this.level, pos.x(), pos.y(), pos.z(), stack);
        this.level.addFreshEntity(entity);
        this.audio = null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (audio != null) {
            pTag.putUUID(PLAYER_ID, audio.player());
            pTag.putUUID(AUDIO_ID, audio.audio());
        }
        if (itemTags != null) {
            pTag.put(TAPE_TAGS, itemTags);
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains(PLAYER_ID)) {
            UUID player = pTag.getUUID(PLAYER_ID);
            UUID audio = pTag.getUUID(AUDIO_ID);
            this.audio = AudioId.of(player, audio);
        }
        if (pTag.contains(TAPE_TAGS)) {
            this.itemTags = pTag.getCompound(TAPE_TAGS);
        }
    }
}
