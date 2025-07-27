package com.example.examplemod.registries;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> THINGY_SPAWN_EGG = REGISTRY.register("thingy_spawn_egg", () -> {
        return new ForgeSpawnEggItem(EntityRegistry.THINGY, 0xdfe610, 0x1b3fff, new Item.Properties().stacksTo(3));
    });

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
