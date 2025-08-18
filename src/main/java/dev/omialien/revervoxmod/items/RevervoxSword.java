package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.IRevervoxEntity;
import dev.omialien.revervoxmod.registries.DamageTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RevervoxSword extends SwordItem implements IRevervoxWeapon {
    private static int getBonusDamage(){
        return RevervoxModServerConfigs.REVERVOX_SWORD_BONUS_DAMAGE.get();
    }

    public RevervoxSword(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pProperties.attributes(SwordItem.createAttributes(Tiers.DIAMOND, pAttackDamageModifier, pAttackSpeedModifier)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Revervox Bonus Damage: " + getBonusDamage()).withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        if(pTarget instanceof IRevervoxEntity){
            pTarget.hurt(pTarget.level().damageSources().source(DamageTypeRegistry.REVERVOX_BONUS), getBonusDamage());
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }


}
