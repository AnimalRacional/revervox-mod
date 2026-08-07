package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class StalkGoal extends Goal {
    private final PathfinderMob mob;
    private StalkState state;
    private int currentStateTimeLeft;
    private boolean handledChange;
    private int circleCooldown = 20;
    public StalkGoal(PathfinderMob mob) {
        super();
        this.mob = mob;
        this.state = StalkState.IDLE;
        this.currentStateTimeLeft = state.time.sample(this.mob.getRandom());
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        this.handledChange = false;
    }

    private void setState(StalkState state){
        RevervoxMod.LOGGER.debug("Setting state to {}", state);
        this.state = state;
        this.handledChange = false;
        this.currentStateTimeLeft = state.time.sample(this.mob.getRandom()) * 20;
    }

    private void setRandomDifferentState() {
        List<StalkState> candidates = StalkState.VALUES.stream()
                .filter(s -> s != this.state)
                .toList();

        StalkState next = candidates.get(this.mob.getRandom().nextInt(candidates.size()));
        this.setState(next);
    }
    enum StalkState{
        IDLE(UniformInt.of(3, 5)),
        CIRCLING_AROUND_TARGET(UniformInt.of(4,8)),
        WALKING_TO_TARGET(UniformInt.of(2,3));
        public final UniformInt time;
        private static final Random rand = new Random();
        private static final List<StalkState> VALUES = List.of(values());
        StalkState(UniformInt time) {
            this.time = time;
        }
        public static StalkState random(){
            return VALUES.get(rand.nextInt(VALUES.size()));
        }
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone()) && this.mob.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            if(this.currentStateTimeLeft > 0) { currentStateTimeLeft--; }
            else { this.setRandomDifferentState(); }

            switch(state) {
                case CIRCLING_AROUND_TARGET -> {
                    circleCooldown--;
                    if(this.mob.getNavigation().isDone() || !handledChange || circleCooldown <= 0){
                        handledChange = true;
                        RevervoxMod.LOGGER.debug("Calculating circle");
                        Vec3 initialDir = this.mob.position().subtract(target.position());
                        Vec3 noYDir = new Vec3(initialDir.x, 0, initialDir.z);
                        Vec3 targetDir = noYDir.normalize().yRot(20).scale(noYDir.length());
                        Vec3 res = target.position().add(targetDir);
                        RevervoxMod.LOGGER.debug("res: {} {} {}", res.x, res.y, res.z);
                        this.mob.getNavigation().moveTo(res.x, res.y, res.z, this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                        circleCooldown = 20;
                    }
                }
                case WALKING_TO_TARGET -> this.mob.getNavigation().moveTo(target, this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                case IDLE -> this.mob.getNavigation().stop();
            }
        }
    }
}