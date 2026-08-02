package dev.omialien.revervoxmod.items.recipes;

import dev.omialien.revervoxmod.items.TapeItem;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.RecipeSerializerRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CopyTapeRecipe extends CustomRecipe {
    // TODO when shift-clicking the result, it will also cause the EmptyTapeRecipe to happen after all tapes are copied
    public CopyTapeRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        ItemStack toCopy = null;
        ItemStack sacrifice = null;
        for(int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (item.getItem() == ItemRegistry.TAPE.get()) {
                if (toCopy == null && TapeItem.hasRecording(item)) {
                    toCopy = item;
                } else if (sacrifice == null) {
                    sacrifice = item;
                } else {
                    return false;
                }
            } else if(!item.isEmpty()) {
                return false;
            }
        }
        return toCopy != null && sacrifice != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        ItemStack toCopy = null;
        ItemStack sacrifice = null;
        for(int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (item.getItem() == ItemRegistry.TAPE.get()) {
                if (toCopy == null && TapeItem.hasRecording(item)) {
                    toCopy = item;
                } else if (sacrifice == null) {
                    sacrifice = item;
                } else {
                    return ItemStack.EMPTY;
                }
            } else if(!item.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        if (toCopy != null && sacrifice != null) {
            return toCopy.copyWithCount(1);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pInv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = pInv.getItem(i);
            if (itemstack.hasCraftingRemainingItem()) {
                nonnulllist.set(i, itemstack.getCraftingRemainingItem());
            } else if (itemstack.getItem() instanceof TapeItem) {
                nonnulllist.set(i, itemstack.copyWithCount(1));
                break;
            }
        }

        return nonnulllist;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.COPY_TAPE.get();
    }


}
