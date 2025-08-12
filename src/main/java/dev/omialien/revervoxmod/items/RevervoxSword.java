package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.IRevervoxEntity;
import dev.omialien.revervoxmod.registries.DamageTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RevervoxSword extends SwordItem implements IRevervoxWeapon {
    private static int getBonusDamage(){
        return RevervoxModServerConfigs.REVERVOX_SWORD_BONUS_DAMAGE.get();
    }

    public RevervoxSword(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Revervox Bonus Damage: " + getBonusDamage()).withStyle(ChatFormatting.BLUE));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        if(pTarget instanceof IRevervoxEntity){
            pTarget.hurt(pTarget.level().damageSources().source(DamageTypeRegistry.REVERVOX_BONUS), getBonusDamage());
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }


}
