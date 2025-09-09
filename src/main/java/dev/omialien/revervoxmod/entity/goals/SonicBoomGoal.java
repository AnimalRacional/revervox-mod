package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.registries.ParticleRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SonicBoomGoal extends Goal {
    private final RevervoxGeoEntity mob;
    public SonicBoomGoal(RevervoxGeoEntity mob){
        this.getFlags().add(Flag.MOVE);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        Player player = this.mob.getTarget() instanceof Player ? (Player) this.mob.getTarget() : null;
        return RevervoxModServerConfigs.REVERVOX_SONIC_BOOM.get() && player != null && !this.mob.hasLineOfSight(player) && this.mob.lostLineOfSightFor(100);
    }

    @Override
    public void start() {
        this.startSonicBoom();
    }

    public void startSonicBoom(){
        this.mob.triggerAnim("Walk/Run/Idle", "SonicBoom");
        if (this.mob.getTarget() == null) return;
        this.mob.lookAt(EntityAnchorArgument.Anchor.FEET, this.mob.getTarget().getEyePosition());
        this.mob.getNavigation().stop();
        int SONIC_BOOM_ANIM_DURATION_TICKS = 45;
        this.mob.level().playSound(null, BlockPos.containing(this.mob.position()), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.0F, 1.0F);
        RevervoxMod.TASKS.schedule(mob::resetNavigation, SONIC_BOOM_ANIM_DURATION_TICKS);
        RevervoxMod.TASKS.schedule(this::doSonicBoom, 20);

    }

    private void doSonicBoom(){
        Vec3 vec3 = this.mob.position().add(this.mob.getAttachments().get(EntityAttachment.WARDEN_CHEST, 0, this.mob.getYRot()));
        if (this.mob.getTarget() == null) return;
        Vec3 targetPosition = this.mob.getTarget().getEyePosition();
        Vec3 vec31 = targetPosition.subtract(vec3);
        Vec3 vec32 = vec31.normalize();
        int i = Mth.floor(vec31.length()) + 7;

        LivingEntity target = this.mob.getTarget();

        for(int j = 1; j < i; ++j) {
            Vec3 vec33 = vec3.add(vec32.scale(j));
            ((ServerLevel)  this.mob.level()).sendParticles(ParticleRegistry.REVERVOX_SONIC_BOOM_PARTICLES.get(), vec33.x, vec33.y, vec33.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        if (target.hurt(this.mob.level().damageSources().sonicBoom(this.mob), 10.0F)) {
            double d1 = 0.5 * (1.0 - (target).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double d0 = 2.5 * (1.0 - (target).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            target.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);

        }
        //this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(), SoundRegistry.MEGAPHONE_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        this.mob.resetLineOfSight();
        this.mob.level().playSound(null, BlockPos.containing(this.mob.position()), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0F, 1.0F);
    }

}
