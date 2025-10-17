package dev.omialien.revervoxmod.items;

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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeTier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RevervoxSword extends SwordItem implements IRevervoxWeapon {
    //TODO: rever oque é level de um tier (n ha no neoforge 1.21.1)
    public static final Tier REVERVOX_TIER = new ForgeTier(
            1, 1796, 8.0f, 3.0f, 10,  BlockTags.NEEDS_DIAMOND_TOOL,() -> Ingredient.of(ItemRegistry.REVERVOX_BAT_TOOTH.get()));
    private static int getBonusDamage(){
        return RevervoxModServerConfigs.REVERVOX_SWORD_BONUS_DAMAGE.get();
    }

    public RevervoxSword(int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(REVERVOX_TIER, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Revervox Bonus Damage: " + getBonusDamage()).withStyle(ChatFormatting.BLUE));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        if(pTarget.getType().is(RevervoxTags.Entities.REVERVOX_BONUS_DAMAGE)){
            pTarget.hurt(pTarget.level().damageSources().source(DamageTypeRegistry.REVERVOX_BONUS), getBonusDamage());
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }


}
