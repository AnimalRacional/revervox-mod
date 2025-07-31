package com.example.revervoxmod.registries;

import com.example.revervoxmod.RevervoxMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegistry {
    private static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RevervoxMod.MOD_ID);

    // TODO no futuro https://youtu.be/N_evngwyOnM?si=mES8YEKdn2f89qTT&t=921 poderá ser bom
    public static final RegistryObject<CreativeModeTab> REVERVOX_MOD_TAB = REGISTRY.register("revervox_mod_tab", () -> {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.revervoxMod"))
                .icon(ItemRegistry.THINGY_SPAWN_EGG.get()::getDefaultInstance)
                .icon(ItemRegistry.REVERVOX_SPAWN_EGG.get()::getDefaultInstance)
                .displayItems((displayParam, output) -> {
                    output.accept(ItemRegistry.THINGY_SPAWN_EGG.get());
                    output.accept(ItemRegistry.REVERVOX_SPAWN_EGG.get());
                }).build();
    });
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
