package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.advancements.RevervoxEatFoodTrigger;
import dev.omialien.revervoxmod.advancements.RevervoxHearTrigger;
import dev.omialien.revervoxmod.advancements.RevervoxStunnedTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class TriggerRegistry {
    private static final DeferredRegister<CriterionTrigger<?>> REGISTRY = DeferredRegister.create(Registries.TRIGGER_TYPE, RevervoxMod.MOD_ID);
    public static final Supplier<RevervoxHearTrigger> HEARD_REVERVOX_TRIGGER = REGISTRY.register("revervox_hears", RevervoxHearTrigger::new);
    public static final Supplier<RevervoxEatFoodTrigger> REVERVOX_ATE_FOOD_TRIGGER = REGISTRY.register("revervox_ate_food", RevervoxEatFoodTrigger::new);
    public static final Supplier<RevervoxStunnedTrigger> REVERVOX_STUNNED_TRIGGER = REGISTRY.register("revervox_stunned", RevervoxStunnedTrigger::new);
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
