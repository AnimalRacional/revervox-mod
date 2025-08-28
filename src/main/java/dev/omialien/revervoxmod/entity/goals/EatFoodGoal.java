package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class EatFoodGoal extends Goal {
    private final Mob entity;
    private final ItemEntity itemTypeToFollow;
    private List<? extends ItemEntity> nearbyItems;
    private ItemEntity currentItemToFollow;
    private final List<ItemEntity> ignoreItems;

    public EatFoodGoal(Mob entity, ItemEntity item) {
        this.entity = entity;
        this.itemTypeToFollow = item;
        this.ignoreItems = new ArrayList<>();
        this.getFlags().add(Goal.Flag.MOVE);
    }
    @Override
    public boolean canUse() {
        AABB entityAABB = this.entity.getBoundingBox().inflate(20, 5, 20);

        if (this.itemTypeToFollow != null) {
            // Remove items from the ignored items list that have been removed
            if (!this.ignoreItems.isEmpty()) this.ignoreItems.removeIf(Entity::isRemoved);

            // Check if there are any items in the area that are of the same type and not in the ignored items list
            nearbyItems = this.entity.level().getEntitiesOfClass(ItemEntity.class, entityAABB,
                    itemEntity -> {
                    boolean flag = itemEntity.getItem().is(itemTypeToFollow.getItem().getItem());
                    boolean flag1 = !this.ignoreItems.contains(itemEntity);
                    return flag && flag1;
                    });
            if (!nearbyItems.isEmpty()) {
                // Sort the items by distance
                nearbyItems.sort((a, b) -> Double.compare(
                        this.entity.distanceToSqr(a),
                        this.entity.distanceToSqr(b)
                ));
                // Check if the closest item is within 20 blocks
                if (this.entity.position().distanceTo(nearbyItems.get(0).position()) > 20.0D) return false;
                RevervoxMod.LOGGER.debug("Nearby Items: " + nearbyItems.size());
                // Get the closest item and set it as the current item to follow
                this.currentItemToFollow = nearbyItems.get(0);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean canContinueToUse() {
        if (entity.getNavigation().isDone() && nearbyItems.contains(currentItemToFollow) && currentItemToFollow.isAlive() && currentItemToFollow != null){
            this.ignoreItems.add(currentItemToFollow);
        }
        return nearbyItems.contains(currentItemToFollow) && currentItemToFollow.isAlive() && currentItemToFollow != null && !entity.getNavigation().isDone();
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

        if (this.entity.position().distanceTo(currentItemToFollow.position()) < 2.5D) {
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
