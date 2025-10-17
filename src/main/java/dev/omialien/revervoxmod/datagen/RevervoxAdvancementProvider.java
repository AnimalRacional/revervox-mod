package dev.omialien.revervoxmod.datagen;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.advancements.RevervoxEatFoodTrigger;
import dev.omialien.revervoxmod.advancements.RevervoxHearTrigger;
import dev.omialien.revervoxmod.advancements.RevervoxStunnedTrigger;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RevervoxAdvancementProvider extends ForgeAdvancementProvider {
    public RevervoxAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AdvancementGenerator()));
    }

    private static final class AdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider provider, Consumer<Advancement> consumer, @NotNull ExistingFileHelper existingFileHelper) {
            System.out.println("GENERATING ADVANCEMENTS!");
            RevervoxMod.LOGGER.debug("GENERATING ADVANCEMENTS!");
            Advancement root = Advancement.Builder.advancement()
                    .display(
                            new ItemStack(ItemRegistry.REVERVOX_VOICE_BOX.get()),
                            Component.translatable("advancements.revervox_mod.root.title"),
                            Component.translatable("advancements.revervox_mod.root.description"),
                            new ResourceLocation(RevervoxMod.MOD_ID, "textures/block/tapebox_side.png"),
                            FrameType.TASK,
                            false,
                            false,
                            false
                    )
                    .addCriterion("layer", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().setY(MinMaxBounds.Doubles.atMost(0)).build()))
                    .requirements(RequirementsStrategy.OR)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/root"), existingFileHelper);
            Advancement revervoxHears = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            // The advancement icon. Can be an ItemStack or an ItemLike.
                            new ItemStack(Items.ECHO_SHARD),
                            // The advancement title and description. Don't forget to add translations for these!
                            Component.translatable("advancements.revervox_mod.revervox_hears.title"),
                            Component.translatable("advancements.revervox_mod.revervox_hears.description"),
                            // The background texture. Use null if you don't want a background texture (for non-root advancements).
                            null,
                            // The frame type. Valid values are FrameType.TASK, CHALLENGE, or GOAL.
                            FrameType.TASK,
                            // Whether to show the advancement toast or not.
                            true,
                            // Whether to announce the advancement into chat or not.
                            true,
                            // Whether the advancement should be hidden or not.
                            false
                    )
                    .addCriterion("revervox_hears", RevervoxHearTrigger.TriggerInstance.getInstance())
                    .requirements(RequirementsStrategy.OR)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/revervox_hears"), existingFileHelper);

            Advancement getEar = Advancement.Builder.advancement()
                    .parent(revervoxHears)
                    .display(
                            new ItemStack(ItemRegistry.REVERVOX_EAR.get()),
                            Component.translatable("advancements.revervox_mod.get_ear.title"),
                            Component.translatable("advancements.revervox_mod.get_ear.description"),
                            null,
                            FrameType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .addCriterion("get_ear", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.REVERVOX_EAR.get()))
                    .addCriterion("kill_revervox", KilledTrigger.TriggerInstance.playerKilledEntity(
                            EntityPredicate.Builder.entity().of(EntityRegistry.REVERVOX.get())
                    ))
                    .requirements(RequirementsStrategy.AND)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/get_ear"), existingFileHelper);
            Advancement craftVoicebox = Advancement.Builder.advancement()
                    .parent(getEar)
                    .display(
                            ItemRegistry.REVERVOX_VOICE_BOX.get(),
                            Component.translatable("advancements.revervox_mod.craft_voice_box.title"),
                            Component.translatable("advancements.revervox_mod.craft_voice_box.description"),
                            null,
                            FrameType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .addCriterion("craft_voicebox", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox_voice_box")))
                    .requirements(RequirementsStrategy.AND)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/craft_voice_box"), existingFileHelper);
            Advancement craftSword = Advancement.Builder.advancement()
                    .parent(craftVoicebox)
                    .display(
                            ItemRegistry.REVERVOX_SWORD.get(),
                            Component.translatable("advancements.revervox_mod.craft_sword.title"),
                            Component.translatable("advancements.revervox_mod.craft_sword.description"),
                            null,
                            FrameType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .rewards(AdvancementRewards.Builder.experience(50))
                    .addCriterion("craft_sword", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox_sword")))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/craft_sword"), existingFileHelper);
            Advancement revervoxEatFood = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ItemRegistry.REVERVOX_BAIT.get(),
                            Component.translatable("advancements.revervox_mod.revervox_ate_food.title"),
                            Component.translatable("advancements.revervox_mod.revervox_ate_food.description"),
                            null,
                            FrameType.TASK,
                            true,
                            true,
                            false
                    )
                    .rewards(AdvancementRewards.Builder.experience(50))
                    .addCriterion("revervox_eat_food", RevervoxEatFoodTrigger.TriggerInstance.getInstance())
                    .requirements(RequirementsStrategy.AND)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/revervox_eat_food"), existingFileHelper);
            Advancement stunRevervox = Advancement.Builder.advancement()
                    .parent(craftVoicebox)
                    .display(
                            ItemRegistry.MEGAPHONE.get(),
                            Component.translatable("advancements.revervox_mod.revervox_stunned.title"),
                            Component.translatable("advancements.revervox_mod.revervox_stunned.description"),
                            null,
                            FrameType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .addCriterion("stun_revervox", RevervoxStunnedTrigger.TriggerInstance.getInstance())
                    .requirements(RequirementsStrategy.AND)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox/revervox_stunned"), existingFileHelper);
        }
    }
}
