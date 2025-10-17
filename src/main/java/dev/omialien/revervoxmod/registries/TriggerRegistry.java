package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.advancements.RevervoxEatFoodTrigger;
import dev.omialien.revervoxmod.advancements.RevervoxHearTrigger;
import dev.omialien.revervoxmod.advancements.RevervoxStunnedTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class TriggerRegistry {
    public static final RevervoxHearTrigger HEARD_REVERVOX_TRIGGER = CriteriaTriggers.register(new RevervoxHearTrigger());
    public static final RevervoxEatFoodTrigger REVERVOX_ATE_FOOD_TRIGGER = CriteriaTriggers.register(new RevervoxEatFoodTrigger());
    public static final RevervoxStunnedTrigger REVERVOX_STUNNED_TRIGGER = CriteriaTriggers.register(new RevervoxStunnedTrigger());
    public static void init() {

    }
}
