package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.advancements.RevervoxHearTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class TriggerRegistry {
    private static final DeferredRegister<CriterionTrigger<?>> REGISTRY = DeferredRegister.create(Registries.TRIGGER_TYPE, RevervoxMod.MOD_ID);
    public static final Supplier<RevervoxHearTrigger> HEARD_REVERVOX_TRIGGER = REGISTRY.register("revervox_hears", RevervoxHearTrigger::new);
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
