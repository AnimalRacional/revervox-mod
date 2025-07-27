package com.example.examplemod.registries;

import com.example.examplemod.ExampleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExampleMod.MOD_ID);
    public static final RegistryObject<SoundEvent> JUMPSCARE = registerSound("jumpscare");

    private static RegistryObject<SoundEvent> registerSound(String name){
        return REGISTRY.register(name, () -> { return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, name)); });
    }
}
