package com.example.revervoxmod.entity.goals;

import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.networking.RevervoxPacketHandler;
import com.example.revervoxmod.networking.packets.AddSoundInstancePacket;
import com.example.revervoxmod.registries.SoundRegistry;
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
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                for(Class<?> oclass : this.toAllowDamage) {
                    if (!oclass.isAssignableFrom(livingentity.getClass())) {
                        return false;
                    }
                }

                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        if (!this.revervox.isAngry()){
            RevervoxPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.mob),
                    new AddSoundInstancePacket(this.mob.getId(), SoundRegistry.REVERVOX_ALERT.get(), SoundSource.HOSTILE));
        }
        timestamp = mob.getLastHurtByMobTimestamp();
        super.start();
    }
}
