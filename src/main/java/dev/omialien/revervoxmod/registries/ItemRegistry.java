package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.AudioRepeatingItem;
import dev.omialien.revervoxmod.items.RevervoxSword;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
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
                    () -> new ForgeSpawnEggItem(EntityRegistry.REVERVOX_BAT, 0xffe591, 0x3b3b3b,
                            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<AudioRepeatingItem> REVERVOX_VOICE_BOX = REGISTRY.register(
            "revervox_voice_box",
            () -> new AudioRepeatingItem(new Item.Properties().stacksTo(1))
    );
    public static final RegistryObject<Item> REVERVOX_EAR = REGISTRY.register(
            "revervox_ear",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<SwordItem> REVERVOX_SWORD = REGISTRY.register(
            "revervox_sword",
            () -> new RevervoxSword(Tiers.DIAMOND, 3, -2.4F, new Item.Properties())
    );
    public static final RegistryObject<Item> REVERVOX_BAT_TEETH = REGISTRY.register(
            "revervox_bat_teeth",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
