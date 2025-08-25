package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.networking.packets.AddSoundInstancePacket;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.PacketDistributor;

public class RevervoxHurtByTargetGoal extends HurtByTargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private final RevervoxGeoEntity revervox;
    private int timestamp;
    private final Class<?>[] toAllowDamage;
    public RevervoxHurtByTargetGoal(RevervoxGeoEntity revervox, Class<?>... toAllowDamage) {
        super(revervox);
        this.toAllowDamage = toAllowDamage;
        this.revervox = revervox;
    }

    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null) {
            RevervoxMod.LOGGER.debug("timestamp + entity;");
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                RevervoxMod.LOGGER.debug("universal anger");
                return false;
            } else {
                boolean found = false;
                for(Class<?> oclass : this.toAllowDamage) {
                    if (oclass.isAssignableFrom(livingentity.getClass())) {
                        found = true;
                        break;
                    }
                }
                if(found){
                    return this.canAttack(livingentity, HURT_BY_TARGETING);
                }else { return false; }
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        RevervoxMod.LOGGER.debug("raah");
        if (!this.revervox.isAngry()){
            revervox.level().playSound(null, revervox.getX(), revervox.getY(), revervox.getZ(), SoundRegistry.REVERVOX_ALERT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            RevervoxPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.mob),
                    new AddSoundInstancePacket(this.mob.getId(), SoundRegistry.REVERVOX_LOOP.get(), SoundSource.HOSTILE, true));
        }
        timestamp = mob.getLastHurtByMobTimestamp();
        super.start();
    }
}
