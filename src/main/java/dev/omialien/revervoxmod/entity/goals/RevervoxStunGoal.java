package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.Supplier;

public class RevervoxStunGoal extends Goal {

    private final RevervoxGeoEntity revervox;
    String animation;
    Supplier<Boolean> canUse;
    int stunTime;
    private int stunTicks = 0;
    private SoundEvent sound;
    private Runnable onStart;
    private Runnable onStop;

    public RevervoxStunGoal(RevervoxGeoEntity revervox, Supplier<Boolean> canUse, String animation, int stunTime, SoundEvent sound, Runnable onStart, Runnable onStop) {
        super();
        this.revervox = revervox;
        this.canUse = canUse;
        this.animation = animation;
        this.stunTime = stunTime;
        this.sound = sound;
        this.onStart = onStart;
        this.onStop = onStop;
        this.getFlags().add(Flag.MOVE);
    }

    @Override
    public boolean canUse() {
        return canUse.get();
    }

    @Override
    public boolean canContinueToUse() {
        return stunTicks > 0;
    }

    @Override
    public void start() {
        RevervoxMod.LOGGER.debug("Starting stun goal");
        revervox.triggerAnim("Walk/Run/Idle", animation);
        revervox.setDeltaMovement(0, 0, 0);
        revervox.getNavigation().stop();
        revervox.level().playSound(null, revervox.getX(), revervox.getY(), revervox.getZ(), this.sound, SoundSource.HOSTILE, 1.0F, 1.0F);
        stunTicks = stunTime;
        onStart.run();
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
        this.onStop.run();
    }
}
