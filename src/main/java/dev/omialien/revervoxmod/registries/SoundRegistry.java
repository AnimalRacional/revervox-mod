package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, RevervoxMod.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> REVERVOX_DEATH = registerSound("revervox_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> THINGY_HURT = registerSound("thingy_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> REVERVOX_HURT = registerSound("revervox_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> REVERVOX_ALERT = registerSound("revervox_alert");
    public static final DeferredHolder<SoundEvent, SoundEvent> REVERVOX_LOOP = registerSound("revervox_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> REVERVOX_BAT_IDLE = registerSound("revervox_bat_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> REVERVOX_BAT_HURT = registerSound("revervox_bat_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> REVERVOX_BAT_ALERT = registerSound("revervox_bat_alert");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name){
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
