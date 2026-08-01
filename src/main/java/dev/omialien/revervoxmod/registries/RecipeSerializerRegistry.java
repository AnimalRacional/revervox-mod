package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.recipes.EmptyTapeRecorderRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeSerializerRegistry {
    private static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS,
            RevervoxMod.MOD_ID
    );
    public static final RegistryObject<RecipeSerializer<EmptyTapeRecorderRecipe>> EMPTY_TAPE = REGISTRY.register(
            "empty_tape_recipe",
            () -> new SimpleCraftingRecipeSerializer<>(EmptyTapeRecorderRecipe::new)
    );


    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
