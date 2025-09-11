package dev.omialien.revervoxmod.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RevervoxEatFoodTrigger extends SimpleCriterionTrigger<RevervoxEatFoodTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<RevervoxEatFoodTrigger.TriggerInstance> codec() {
        return RevervoxEatFoodTrigger.TriggerInstance.CODEC;
    }
    public void trigger(ServerPlayer player){
        this.trigger(player, (p) -> true);
    }
    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static Codec<RevervoxEatFoodTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(RevervoxEatFoodTrigger.TriggerInstance::player)
                ).apply(builder, RevervoxEatFoodTrigger.TriggerInstance::new)
        );
    }

}
