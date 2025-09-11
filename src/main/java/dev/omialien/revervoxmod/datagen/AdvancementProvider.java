package dev.omialien.revervoxmod.datagen;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.advancements.RevervoxEatFoodTrigger;
import dev.omialien.revervoxmod.advancements.RevervoxHearTrigger;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.TriggerRegistry;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends net.neoforged.neoforge.common.data.AdvancementProvider {

    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AdvancementGenerator()));
    }

    private static final class AdvancementGenerator implements net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
            System.out.println("GENERATING ADVANCEMENTS!");
            RevervoxMod.LOGGER.debug("GENERATING ADVANCEMENTS!");
            AdvancementHolder root = new Advancement.Builder()
                    .display(
                            new ItemStack(ItemRegistry.REVERVOX_VOICE_BOX.get()),
                            Component.translatable("advancements.revervox_mod.root.title"),
                            Component.translatable("advancements.revervox_mod.root.description"),
                            ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "textures/block/tapebox_side.png"),
                            AdvancementType.TASK,
                            false,
                            false,
                            false
                    )
                    .addCriterion("layer", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().setY(MinMaxBounds.Doubles.atMost(0))))
                    .requirements(AdvancementRequirements.allOf(List.of("layer")))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/root"), existingFileHelper);
            AdvancementHolder revervoxHears = new Advancement.Builder()
                    .parent(root)
                .display(
                    // The advancement icon. Can be an ItemStack or an ItemLike.
                    new ItemStack(Items.ECHO_SHARD),
                    // The advancement title and description. Don't forget to add translations for these!
                    Component.translatable("advancements.revervox_mod.revervox_hears.title"),
                    Component.translatable("advancements.revervox_mod.revervox_hears.description"),
                    // The background texture. Use null if you don't want a background texture (for non-root advancements).
                    null,
                    // The frame type. Valid values are AdvancementType.TASK, CHALLENGE, or GOAL.
                    AdvancementType.TASK,
                    // Whether to show the advancement toast or not.
                    true,
                    // Whether to announce the advancement into chat or not.
                    true,
                    // Whether the advancement should be hidden or not.
                    false
                )
            .addCriterion("revervox_hears", TriggerRegistry.HEARD_REVERVOX_TRIGGER.get().createCriterion(new RevervoxHearTrigger.TriggerInstance(Optional.empty())))
            .requirements(AdvancementRequirements.allOf(List.of("revervox_hears")))
            .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/revervox_hears"), existingFileHelper);

            AdvancementHolder getEar = new Advancement.Builder()
                    .parent(revervoxHears)
                    .display(
                            new ItemStack(ItemRegistry.REVERVOX_EAR.get()),
                            Component.translatable("advancements.revervox_mod.get_ear.title"),
                            Component.translatable("advancements.revervox_mod.get_ear.description"),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .addCriterion("get_ear", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.REVERVOX_EAR.get()))
                    .addCriterion("kill_revervox", KilledTrigger.TriggerInstance.playerKilledEntity(
                            EntityPredicate.Builder.entity().of(EntityRegistry.REVERVOX.get())
                    ))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/get_ear"), existingFileHelper);
            AdvancementHolder craftVoicebox = new Advancement.Builder()
                    .parent(getEar)
                    .display(
                            ItemRegistry.REVERVOX_VOICE_BOX.get(),
                            Component.translatable("advancements.revervox_mod.craft_voice_box.title"),
                            Component.translatable("advancements.revervox_mod.craft_voice_box.description"),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .addCriterion("craft_voicebox", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox_voice_box")))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/craft_voice_box"), existingFileHelper);
            AdvancementHolder craftSword = new Advancement.Builder()
                    .parent(craftVoicebox)
                    .display(
                            ItemRegistry.REVERVOX_SWORD.get(),
                            Component.translatable("advancements.revervox_mod.craft_sword.title"),
                            Component.translatable("advancements.revervox_mod.craft_sword.description"),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .rewards(AdvancementRewards.Builder.experience(50))
                    .addCriterion("craft_sword", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox_sword")))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/craft_sword"), existingFileHelper);
            AdvancementHolder revervoxEatFood = new Advancement.Builder()
                    .parent(root)
                    .display(
                            ItemRegistry.REVERVOX_BAIT.get(),
                            Component.translatable("advancements.revervox_mod.revervox_ate_food.title"),
                            Component.translatable("advancements.revervox_mod.revervox_ate_food.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .rewards(AdvancementRewards.Builder.experience(50))
                    .addCriterion("revervox_eat_food", TriggerRegistry.REVERVOX_ATE_FOOD_TRIGGER.get().createCriterion(new RevervoxEatFoodTrigger.TriggerInstance(Optional.empty())))
                    .requirements(AdvancementRequirements.allOf(List.of("revervox_eat_food")))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/revervox_eat_food"), existingFileHelper);
        }
    }
}
