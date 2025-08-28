package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;
//TODO se puser a comida num pillar ele fica preso a tentar ir buscar a comida
public class EatFoodGoal extends Goal {
    private final Mob entity;
    private final ItemEntity itemTypeToFollow;
    private List<? extends ItemEntity> nearbyItems;
    private ItemEntity currentItemToFollow;

    public EatFoodGoal(Mob entity, ItemEntity item) {
        this.entity = entity;
        this.itemTypeToFollow = item;
        this.getFlags().add(Goal.Flag.MOVE);
    }
    @Override
    public boolean canUse() {
        AABB entityAABB = this.entity.getBoundingBox().inflate(20, 5, 20);
        //RevervoxMod.LOGGER.debug("AABB: " + entityAABB);
        if (this.itemTypeToFollow != null) {
            nearbyItems = this.entity.level().getEntitiesOfClass(ItemEntity.class, entityAABB,
                    itemEntity -> itemEntity.getItem().is(itemTypeToFollow.getItem().getItem()));
            if (!nearbyItems.isEmpty()) {

                nearbyItems.sort((a, b) -> Double.compare(
                        this.entity.distanceToSqr(a),
                        this.entity.distanceToSqr(b)
                ));
                if (this.entity.position().distanceTo(nearbyItems.get(0).position()) > 20.0D) return false;
                RevervoxMod.LOGGER.debug("Nearby Items: " + nearbyItems.size());
                this.currentItemToFollow = nearbyItems.get(0);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean canContinueToUse() {
        return nearbyItems.contains(currentItemToFollow) && currentItemToFollow.isAlive() && currentItemToFollow != null;
    }

    @Override
    public void start() {
        super.start();
        if (currentItemToFollow != null) {
            entity.getNavigation().moveTo(currentItemToFollow,
                    Objects.requireNonNull(entity.getAttribute(Attributes.MOVEMENT_SPEED)).getValue());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (currentItemToFollow == null || !currentItemToFollow.isAlive()) return;

        if (entity.getNavigation().isDone() ||
                entity.position().distanceToSqr(currentItemToFollow.position()) > 25.0D) {
            entity.getNavigation().moveTo(currentItemToFollow,
                    Objects.requireNonNull(entity.getAttribute(Attributes.MOVEMENT_SPEED)).getValue());
        }

        if (this.entity.position().distanceTo(currentItemToFollow.position()) < 1.5D) {
            RevervoxMod.LOGGER.debug("Eating");
            this.entity.eat(this.entity.level(), currentItemToFollow.getItem());
            currentItemToFollow.discard();
            this.nearbyItems.remove(currentItemToFollow);

        }
    }

    @Override
    public void stop() {
        super.stop();
        RevervoxMod.LOGGER.debug("Stopping EatFoodGoal");
    }
}
