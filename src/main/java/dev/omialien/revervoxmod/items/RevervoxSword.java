package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.registries.DamageTypeRegistry;
import dev.omialien.revervoxmod.registries.ItemRegistry;
import dev.omialien.revervoxmod.registries.RevervoxTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RevervoxSword extends SwordItem {
    public static final Tier REVERVOX_TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1796, 8.0f, 3.0f, 10,  () -> Ingredient.of(ItemRegistry.REVERVOX_BAT_TOOTH.get()));

    private static int getBonusDamage(){
        return RevervoxModServerConfigs.REVERVOX_SWORD_BONUS_DAMAGE.get();
    }

    public RevervoxSword(int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(REVERVOX_TIER, pProperties.attributes(SwordItem.createAttributes(REVERVOX_TIER, pAttackDamageModifier, pAttackSpeedModifier)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Revervox Bonus Damage: " + getBonusDamage()).withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        if(pTarget.getType().is(RevervoxTags.Entities.REVERVOX_BONUS_DAMAGE)){
            RevervoxMod.LOGGER.debug("Dealing bonus damage!");
            pTarget.hurt(pTarget.level().damageSources().source(DamageTypeRegistry.REVERVOX_BONUS), getBonusDamage());
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }
}
