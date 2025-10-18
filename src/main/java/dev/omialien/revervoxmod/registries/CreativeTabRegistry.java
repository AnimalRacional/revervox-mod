package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreativeTabRegistry {
    private static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RevervoxMod.MOD_ID);
    private static final List<Supplier<? extends ItemLike>> TAB_ITEMS = new ArrayList<>();
    public static final RegistryObject<CreativeModeTab> REVERVOX_MOD_TAB = REGISTRY.register("revervox_mod_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.revervoxMod"))
            .icon(ItemRegistry.REVERVOX_VOICE_BOX.get()::getDefaultInstance)
            .withLabelColor(4)
            .displayItems((displayParam, output) -> TAB_ITEMS.forEach(i -> output.accept(i.get()))).build());
    public static <T extends Item> void addToTab(RegistryObject<T> item) {
        TAB_ITEMS.add(item);
    }
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
