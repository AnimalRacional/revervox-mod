package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.registries.TriggerRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EatFoodGoal extends Goal {
    private final Mob entity;
    private final TagKey<Item> itemTypesToFollow;
    private List<? extends ItemEntity> nearbyItems;
    private ItemEntity currentItemToFollow;
    private final Set<ItemEntity> ignoreItems;

    public EatFoodGoal(Mob entity, TagKey<Item> item) {
        this.entity = entity;
        this.itemTypesToFollow = item;
        this.ignoreItems = new HashSet<>();
        this.getFlags().add(Flag.MOVE);
    }
    @Override
    public boolean canUse() {
        AABB entityAABB = this.entity.getBoundingBox().inflate(20, 5, 20);
        // TODO este null check é necessário?
        if (this.itemTypesToFollow != null) {
            // Remove items from the ignored items list that have been removed
            if (!this.ignoreItems.isEmpty()) this.ignoreItems.removeIf(Entity::isRemoved);
            // Check if there are any items in the area that are of the same type and not in the ignored items list
            nearbyItems = this.entity.level().getEntitiesOfClass(ItemEntity.class, entityAABB,
                    itemEntity -> {
                        boolean isValidItem = itemEntity.getItem().is(itemTypesToFollow);
                        boolean isIgnored = this.ignoreItems.contains(itemEntity);
                        return isValidItem && !isIgnored;
                    });
            if (!nearbyItems.isEmpty()) {
                // TODO em vez de dar sort ao array, não se pode só fazer um loop e encontrar o mais pequeno?
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
        if(this.currentItemToFollow == null) { return false; }
        if (entity.getNavigation().isDone() && nearbyItems.contains(currentItemToFollow) && currentItemToFollow.isAlive()){
            this.ignoreItems.add(currentItemToFollow);
        }
        return nearbyItems.contains(currentItemToFollow) && currentItemToFollow.isAlive() && !entity.getNavigation().isDone();
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
            if(currentItemToFollow.getItem().getFoodProperties(this.entity) != null){
                this.entity.eat(this.entity.level(), currentItemToFollow.getItem());
            } else {
                this.entity.level().playSound(null, this.entity.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.HOSTILE);
            }
            if(currentItemToFollow.getOwner() instanceof ServerPlayer spTarget){
                TriggerRegistry.REVERVOX_ATE_FOOD_TRIGGER.get().trigger(spTarget);
            }
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
