package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreativeTabRegistry {
    private static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RevervoxMod.MOD_ID);

    // TODO no futuro https://youtu.be/N_evngwyOnM?si=mES8YEKdn2f89qTT&t=921 poderá ser bom
    private static final List<Supplier<? extends ItemLike>> TAB_ITEMS = new ArrayList<>();
    public static final Supplier<CreativeModeTab> REVERVOX_MOD_TAB = REGISTRY.register("revervox_mod_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.revervox_mod"))
            .icon(ItemRegistry.REVERVOX_VOICE_BOX.get()::getDefaultInstance)
            .withLabelColor(4)
            .displayItems((displayParam, output) -> {
                RevervoxMod.LOGGER.debug("accepting {} creative items", TAB_ITEMS.size());
                TAB_ITEMS.forEach(s -> output.accept(s.get()));
            }).build());
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
    public static <T extends ItemLike> void addToTab(Supplier<T> item) {
        TAB_ITEMS.add(item);
    }
}
