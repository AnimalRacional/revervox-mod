package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.IRevervoxEntity;
import dev.omialien.revervoxmod.registries.DamageTypeRegistry;
import dev.omialien.voicechat_recording.voicechat.RecordedAudio;
import dev.omialien.voicechat_recording.voicechat.util.AudioPlayingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
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

    @Override
    public void postHurtEnemy(@NotNull ItemStack stack, LivingEntity target, @NotNull LivingEntity attacker) {
        if(target.level() instanceof ServerLevel serverLevel){
            if(target instanceof Player plr && target.isDeadOrDying()){
                RecordedAudio audio = RevervoxMod.AUDIOS.getRandomAudio(plr.getUUID(), false);
                if(audio != null){
                    AudioPlayingUtil.playLocationalAudio(audio, target.position(), serverLevel, RevervoxMod.MOD_ID);
                }
            }
        }
        super.postHurtEnemy(stack, target, attacker);
    }
}
