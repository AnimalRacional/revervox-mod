package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class SonicBoomGoal extends Goal {
    private final RevervoxGeoEntity mob;
    public SonicBoomGoal(RevervoxGeoEntity mob){
        this.getFlags().add(Flag.MOVE);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        Player player = this.mob.getTarget() instanceof Player ? (Player) this.mob.getTarget() : null;
        return player != null && !this.mob.hasLineOfSight(player) && this.mob.lostLineOfSightFor(200);
    }

    @Override
    public void start() {
        RevervoxMod.LOGGER.debug("Starting SonicBoomGoal");
        this.mob.startSonicBoom();
    }

    @Override
    public void stop() {
        RevervoxMod.LOGGER.debug("Stopping SonicBoomGoal");
        this.mob.resetLineOfSight();
    }
}
