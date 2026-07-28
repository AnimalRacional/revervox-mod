package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.registries.TriggerRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
    private final RevervoxGeoEntity entity;
    private final TagKey<Item> itemTypesToFollow;
    private List<? extends ItemEntity> nearbyItems;
    private ItemEntity currentItemToFollow;
    private final Set<ItemEntity> ignoreItems;
    private static final int EAT_ANIM_DURATION_TICKS = 75;
    private boolean eating = false;

    public EatFoodGoal(RevervoxGeoEntity entity, TagKey<Item> item) {
        this.entity = entity;
        this.itemTypesToFollow = item;
        this.ignoreItems = new HashSet<>();
        this.getFlags().add(Flag.MOVE);
    }
    @Override
    public boolean canUse() {
        AABB entityAABB = this.entity.getBoundingBox().inflate(20, 5, 20);
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
            // Get the closest item
            ItemEntity closestItem = null;

            for (ItemEntity item : nearbyItems) {
                if (closestItem == null) {
                    closestItem = item;
                } else {
                    if (this.entity.distanceToSqr(item) < this.entity.distanceToSqr(closestItem)) {
                        closestItem = item;
                    }
                }
            }

            // Check if the closest item is within 20 blocks
            if (this.entity.position().distanceTo(closestItem.position()) > 20.0D) return false;
            RevervoxMod.LOGGER.debug("Nearby Items: " + nearbyItems.size());
            // Get the closest item and set it as the current item to follow
            this.currentItemToFollow = closestItem;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (eating) {return true;}
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
            if(currentItemToFollow.getItem().getFoodProperties(this.entity) != null){
                RevervoxMod.LOGGER.debug("Eating");
                this.entity.eat(this.entity.level(), currentItemToFollow.getItem());
                handleEating();
            } else {
                RevervoxMod.LOGGER.debug("Playing sound");
                this.entity.level().playSound(null, this.entity.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.HOSTILE);
                handleEating();
            }
            if(currentItemToFollow.getOwner() instanceof ServerPlayer spTarget){
                TriggerRegistry.REVERVOX_ATE_FOOD_TRIGGER.get().trigger(spTarget);
            }
            currentItemToFollow.discard();
            this.nearbyItems.remove(currentItemToFollow);
        }
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void handleEating(){
        if (!eating){
            this.entity.triggerAnim("Walk/Run/Idle", "Eat");
            this.entity.setDeltaMovement(0, 0, 0);
            this.entity.getNavigation().stop();
            this.eating = true;
            RevervoxMod.LOGGER.debug("Set eating to true");
            RevervoxMod.TASKS.schedule(this::resetEating, EAT_ANIM_DURATION_TICKS);
        }
    }

    private void resetEating(){
        RevervoxMod.LOGGER.debug("Reset eating");
        this.eating = false;
    }

    @Override
    public void stop() {
        super.stop();
        RevervoxMod.LOGGER.debug("Stopping eating goal");
        this.entity.resetNavigation();
        this.checkForTarget();
    }

    private void checkForTarget(){
        LivingEntity target = this.entity.getTarget();
        if(target != null){
            if (this.entity.distanceTo(target) > 30.0D) {
                this.entity.setTarget(null);
            }
        }
    }
}
