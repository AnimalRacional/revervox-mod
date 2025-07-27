package com.example.examplemod.registries;

import com.example.examplemod.ExampleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExampleMod.MOD_ID);
    public static final RegistryObject<SoundEvent> JUMPSCARE = registerSound("jumpscare");
    public static final RegistryObject<SoundEvent> THINGY_HURT = registerSound("thingy_hurt");

    private static RegistryObject<SoundEvent> registerSound(String name){
        return REGISTRY.register(name, () -> { return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, name)); });
    }

    public static void register(IEventBus bus){
        REGISTRY.register(bus);
    }
}
