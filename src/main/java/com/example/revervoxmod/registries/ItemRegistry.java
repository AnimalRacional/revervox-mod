package com.example.revervoxmod.registries;

import com.example.revervoxmod.RevervoxMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, RevervoxMod.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> THINGY_SPAWN_EGG = REGISTRY.register("thingy_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.THINGY, 0xdfe610, 0x1b3fff, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> REVERVOX_SPAWN_EGG = REGISTRY.register("revervox_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.REVERVOX_GEO, 0x3b3b3b, 0xffe591, new Item.Properties().stacksTo((64))));
    public static final RegistryObject<ForgeSpawnEggItem> REVERVOX_BAT_SPAWN_EGG = REGISTRY
            .register("revervox_bat_spawn_egg",
                    () -> new ForgeSpawnEggItem(EntityRegistry.REVERVOX_BAT, 0xffffff, 0x000000,
                            new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
