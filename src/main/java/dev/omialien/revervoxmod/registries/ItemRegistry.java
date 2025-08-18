package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.AudioRepeatingItem;
import dev.omialien.revervoxmod.items.RevervoxSword;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    private static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(RevervoxMod.MOD_ID);

    public static final DeferredItem<DeferredSpawnEggItem> THINGY_SPAWN_EGG = REGISTRY.register("thingy_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.THINGY, 0xdfe610, 0x1b3fff, new Item.Properties()));
    public static final DeferredItem<DeferredSpawnEggItem> REVERVOX_SPAWN_EGG = REGISTRY.register("revervox_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.REVERVOX, 0x3b3b3b, 0xffe591, new Item.Properties()));
    public static final DeferredItem<DeferredSpawnEggItem> REVERVOX_BAT_SPAWN_EGG = REGISTRY
            .register("revervox_bat_spawn_egg",
                    () -> new DeferredSpawnEggItem(EntityRegistry.REVERVOX_BAT, 0xffe591, 0x3b3b3b,
                            new Item.Properties()));
    public static final DeferredItem<AudioRepeatingItem> REVERVOX_VOICE_BOX = REGISTRY.register(
            "revervox_voice_box",
            () -> new AudioRepeatingItem(new Item.Properties().stacksTo(1))
    );
    public static final DeferredItem<Item> REVERVOX_EAR = REGISTRY.register(
            "revervox_ear",
            () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<SwordItem> REVERVOX_SWORD = REGISTRY.register(
            "revervox_sword",
            () -> new RevervoxSword(Tiers.DIAMOND, 3, -2.4F, new Item.Properties())
    );
    public static final DeferredItem<Item> REVERVOX_BAT_TOOTH = REGISTRY.register(
            "revervox_bat_tooth",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
