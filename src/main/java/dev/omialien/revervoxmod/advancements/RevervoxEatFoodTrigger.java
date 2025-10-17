package dev.omialien.revervoxmod.advancements;

import com.google.gson.JsonObject;
import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class RevervoxEatFoodTrigger extends SimpleCriterionTrigger<RevervoxEatFoodTrigger.TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation(RevervoxMod.MOD_ID, "revervox_ate_food");
    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    protected @NotNull TriggerInstance createInstance(@NotNull JsonObject pJson, @NotNull ContextAwarePredicate pPredicate, @NotNull DeserializationContext pDeserializationContext) {
        return new TriggerInstance(pPredicate);
    }

    public void trigger(ServerPlayer player){
        this.trigger(player, (p) -> true);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ContextAwarePredicate pPlayer) {
            super(ID, pPlayer);
        }

        public static TriggerInstance getInstance() {
            return new TriggerInstance(ContextAwarePredicate.ANY);
        }
    }

}
