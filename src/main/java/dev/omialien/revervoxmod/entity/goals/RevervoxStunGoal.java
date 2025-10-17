package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;

public class RevervoxStunGoal extends Goal {

    private final RevervoxGeoEntity revervox;
    private static final int STUN_ANIM_DURATION_TICKS = 55;
    private int stunTicks = 0;

    public RevervoxStunGoal(RevervoxGeoEntity revervox) {
        super();
        this.revervox = revervox;
        this.getFlags().add(Flag.MOVE);
    }

    @Override
    public boolean canUse() {
        return this.revervox.isStunned();
    }

    @Override
    public boolean canContinueToUse() {
        return stunTicks > 0;
    }

    @Override
    public void start() {
        RevervoxMod.LOGGER.debug("Starting stun goal");
        revervox.triggerAnim("Walk/Run/Idle", "Stun");
        revervox.setDeltaMovement(0, 0, 0);
        revervox.getNavigation().stop();
        revervox.level().playSound(null, revervox.getX(), revervox.getY(), revervox.getZ(), SoundRegistry.REVERVOX_STUN.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        stunTicks = STUN_ANIM_DURATION_TICKS;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        stunTicks--;
    }

    @Override
    public void stop() {
        super.stop();
        RevervoxMod.LOGGER.debug("Stopping stun goal");
        revervox.setStunned(false);
        stunTicks = 0;
        revervox.resetNavigation();
    }
}
