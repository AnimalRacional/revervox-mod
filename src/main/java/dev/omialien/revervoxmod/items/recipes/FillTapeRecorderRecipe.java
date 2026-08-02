package dev.omialien.revervoxmod.items.recipes;

import dev.omialien.revervoxmod.items.TapeItem;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.RecipeSerializerRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FillTapeRecorderRecipe extends CustomRecipe {
    public FillTapeRecorderRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, @NotNull Level pLevel) {
        ItemStack tape = null;
        ItemStack recorder = null;
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (item.getItem() == ItemRegistry.TAPE_RECORDER_OFF.get()) {
                if (recorder != null) {
                    return false;
                }
                recorder = item;
            } else if (item.getItem() == ItemRegistry.TAPE.get()) {
                if (tape != null) {
                    return false;
                }
                tape = item;
            } else if (!item.isEmpty()) {
                return false;
            }
        }
        return tape != null && recorder != null;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer pContainer, @NotNull RegistryAccess pRegistryAccess) {
        ItemStack tape = null;
        ItemStack recorder = null;
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (item.getItem() == ItemRegistry.TAPE_RECORDER_OFF.get()) {
                if (recorder != null) {
                    return ItemStack.EMPTY;
                }
                recorder = item;
            } else if (item.getItem() == ItemRegistry.TAPE.get()) {
                if (tape != null) {
                    return ItemStack.EMPTY;
                }
                tape = item;
            } else if (!item.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        if (tape == null || recorder == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(ItemRegistry.TAPE_RECORDER_ON.get());
        CompoundTag tag = tape.getTag();
        CompoundTag newTag = new CompoundTag();
        if (tag != null) {
            newTag.put(TapeItem.TAPE_COMPONENTS, tag);
        }
        result.setTag(newTag);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.FILL_RECORDER.get();
    }
}
