package com.example.examplemod.registries;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegistry {
    private static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExampleMod.MOD_ID);

    // TODO no futuro https://youtu.be/N_evngwyOnM?si=mES8YEKdn2f89qTT&t=921 poderá ser bom
    public static final RegistryObject<CreativeModeTab> EXAMPLE_MOD_TAB = REGISTRY.register("example_mod_tab", () -> {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.exampleMod"))
                .icon(ItemRegistry.THINGY_SPAWN_EGG.get()::getDefaultInstance)
                .displayItems((displayParam, output) -> {
                    output.accept(ItemRegistry.THINGY_SPAWN_EGG.get());
                }).build();
    });
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
