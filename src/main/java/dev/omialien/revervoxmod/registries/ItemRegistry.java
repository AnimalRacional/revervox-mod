package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemRegistry {
    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, RevervoxMod.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> THINGY_SPAWN_EGG = register(
            "thingy_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.THINGY, 0xdfe610, 0x1b3fff, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> REVERVOX_SPAWN_EGG = register(
            "revervox_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.REVERVOX, 0x3b3b3b, 0xffe591, new Item.Properties().stacksTo((64))));
    public static final RegistryObject<ForgeSpawnEggItem> REVERVOX_BAT_SPAWN_EGG = register(
            "revervox_bat_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.REVERVOX_BAT, 0xffe591, 0x3b3b3b,
                new Item.Properties().stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> STRIDORVOX_SPAWN_EGG = register(
            "stridorvox_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.STRIDORVOX, 0x341d1b, 0xe78786, new Item.Properties().stacksTo((64))));
    public static final RegistryObject<RevervoxVoiceBoxItem> REVERVOX_VOICE_BOX = register(
            "revervox_voice_box",
            () -> new RevervoxVoiceBoxItem(new Item.Properties().stacksTo(1))
    );
    public static final RegistryObject<RevervoxBaitItem> REVERVOX_BAIT = register(
            "revervox_bait",
            () -> new RevervoxBaitItem(new Item.Properties().stacksTo(16))
    );
    public static final RegistryObject<Item> REVERVOX_EAR = register(
            "revervox_ear",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<MegaphoneItem> MEGAPHONE = register(
            "megaphone",
            () -> new MegaphoneItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<SwordItem> REVERVOX_SWORD = register(
            "revervox_sword",
            () -> new RevervoxSword(3, -2.4F, new Item.Properties())
    );
    public static final RegistryObject<Item> REVERVOX_BAT_TOOTH = register(
            "revervox_bat_tooth",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static final RegistryObject<Item> TAPE_RECORDER_ON = register(
            "tape_recorder_on",
            () -> new TapeRecorderItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> TAPE_RECORDER_OFF = register(
            "tape_recorder_off",
            () -> new TapeRecorderOffItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> TAPE = register(
            "tape",
            () -> new TapeItem(new Item.Properties())
    );

    public static final RegistryObject<Item> ECHO_DARK_SURFACE_BLOCK = register(
            "echo_dark_surface_block",
            () -> new BlockItem(BlockRegistry.ECHO_DARK_SURFACE_BLOCK.get(), new Item.Properties().stacksTo(64))
    );
    public static final RegistryObject<Item> TAPEBOX = register(
            "tapebox",
            () -> new BlockItem(BlockRegistry.TAPEBOX.get(), new Item.Properties().stacksTo(64))
    );
    public static final RegistryObject<Item> ECHO_DARK_GRASS = register(
            "echo_dark_grass",
            () -> new BlockItem(BlockRegistry.ECHO_DARK_GRASS.get(), new Item.Properties().stacksTo(64)), false
    );
    public static final RegistryObject<Item> ECHO_DARK_PLANT = register(
            "echo_dark_plant",
            () -> new BlockItem(BlockRegistry.ECHO_DARK_PLANT.get(), new Item.Properties().stacksTo(64)), false
    );

    public static final RegistryObject<Item> GUANO_BLOCK = register(
            "guano_block",
            () -> new BlockItem(BlockRegistry.GUANO_BLOCK.get(), new Item.Properties().stacksTo(64))
    );

    public static final RegistryObject<Item> GUANO = register(
            "guano",
            () -> new BlockItem(BlockRegistry.GUANO.get(), new Item.Properties().stacksTo(64))
    );
    public static final RegistryObject<BlockItem> ECHO_DARK_VEIN = register(
            "echo_dark_vein",
            () -> new BlockItem(BlockRegistry.ECHO_DARK_VEIN_BLOCK.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> NIGHTMARE_CHEST = register(
            "nightmare_chest",
            () -> new NightmareChestItem(BlockRegistry.NIGHTMARE_CHEST.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> ECHO_DARK_BRICKS = register(
            "echo_dark_bricks",
            () -> new BlockItem(BlockRegistry.ECHO_DARK_BRICKS.get(), new Item.Properties().stacksTo(64))
    );
    public static final RegistryObject<Item> ECHO_DARK_BRICKS_STAIRS = register(
            "echo_dark_bricks_stairs",
            () -> new BlockItem(BlockRegistry.ECHO_DARK_BRICKS_STAIRS.get(), new Item.Properties().stacksTo(64))
    );
    public static final RegistryObject<Item> ECHO_DARK_BRICKS_SLAB = register(
            "echo_dark_bricks_slab",
            () -> new BlockItem(BlockRegistry.ECHO_DARK_BRICKS_SLAB.get(), new Item.Properties().stacksTo(64))
    );

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item, boolean inTab) {
        RegistryObject<T> reg = REGISTRY.register(name, item);
        if (inTab) {
            CreativeTabRegistry.addToTab(reg);
        }
        return reg;
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ItemRegistry.register(name, item, true);
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
