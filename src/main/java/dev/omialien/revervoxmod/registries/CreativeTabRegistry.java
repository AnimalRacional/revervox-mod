package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegistry {
    private static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RevervoxMod.MOD_ID);

    // TODO no futuro https://youtu.be/N_evngwyOnM?si=mES8YEKdn2f89qTT&t=921 poderá ser bom
    public static final RegistryObject<CreativeModeTab> REVERVOX_MOD_TAB = REGISTRY.register("revervox_mod_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.revervoxMod"))
            .icon(ItemRegistry.REVERVOX_VOICE_BOX.get()::getDefaultInstance)
            .withLabelColor(4)
            .displayItems((displayParam, output) -> {
                output.accept(ItemRegistry.THINGY_SPAWN_EGG.get());
                output.accept(ItemRegistry.REVERVOX_SPAWN_EGG.get());
                output.accept(ItemRegistry.REVERVOX_BAT_SPAWN_EGG.get());
                output.accept(ItemRegistry.REVERVOX_VOICE_BOX.get());
                output.accept(ItemRegistry.REVERVOX_EAR.get());
                output.accept(ItemRegistry.REVERVOX_SWORD.get());
                output.accept(ItemRegistry.REVERVOX_BAT_TOOTH.get());
            }).build());
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
