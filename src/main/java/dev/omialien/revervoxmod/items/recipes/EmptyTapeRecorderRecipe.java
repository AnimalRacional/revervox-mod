package dev.omialien.revervoxmod.items.recipes;

import dev.omialien.revervoxmod.items.TapeRecorderItem;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.RecipeSerializerRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EmptyTapeRecorderRecipe extends CustomRecipe {
    public EmptyTapeRecorderRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        ItemStack stack = ItemStack.EMPTY;
        for(int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (!item.isEmpty()) {
                if (!stack.isEmpty() || item.getItem() != ItemRegistry.TAPE_RECORDER.get() || !TapeRecorderItem.hasRecording(item)) {
                    return false;
                }
                stack = item;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        ItemStack stack = ItemStack.EMPTY;
        for(int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (!item.isEmpty()) {
                if (!stack.isEmpty() || item.getItem() != ItemRegistry.TAPE_RECORDER.get() || !TapeRecorderItem.hasRecording(item)) {
                    return ItemStack.EMPTY;
                }
                stack = item;
            }
        }
        ItemStack res = stack.copy();
        res.removeTagKey(TapeRecorderItem.PLAYER_ID);
        res.removeTagKey(TapeRecorderItem.AUDIO_ID);
        return res;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight > 0;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.EMPTY_TAPE.get();
    }
}
