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

public class RevervoxStunnedTrigger extends SimpleCriterionTrigger<RevervoxStunnedTrigger.TriggerInstance> {
    public static final ResourceLocation ID = new ResourceLocation(RevervoxMod.MOD_ID, "revervox_stunned");
    // TODO add revervox stunned statistic count?
    public void trigger(ServerPlayer player){
        this.trigger(player, (p) -> true);
    }

    @Override
    protected @NotNull TriggerInstance createInstance(@NotNull JsonObject pJson, @NotNull ContextAwarePredicate pPredicate, @NotNull DeserializationContext pDeserializationContext) {
        return new TriggerInstance(pPredicate);
    }
    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ContextAwarePredicate pPlayer) {
            super(ID, pPlayer);
        }
    }
}
