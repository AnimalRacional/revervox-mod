package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.StridorvoxEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class StridorvoxStealItemsGoal extends Goal {
    private final StridorvoxEntity stridorvox;
    protected PathNavigation pathNav;
    protected Player nearestPlayer;
    protected Path path = null;
    protected int lastDamageTime = 0;
    protected int damageCount = 0;

    public StridorvoxStealItemsGoal(StridorvoxEntity stridorvox) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.TARGET));
        this.stridorvox = stridorvox;
        this.pathNav = stridorvox.getNavigation();
    }

    public boolean canUse() {
        if (!stridorvox.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
        }
        if (stridorvox.getRandom().nextInt(reducedTickDelay(10)) != 0) {
            return false;
        } else {
            List<Player> list = stridorvox.level().getEntitiesOfClass(Player.class, stridorvox.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
            if (list.isEmpty()) {
                return false;
            }
            this.nearestPlayer = list.get(0);
            if (nearestPlayer.isCreative()) {
                return false;
            }
            if (stridorvox.distanceToSqr(nearestPlayer) > 5.0D) {
                return false;
            }

            if (!stridorvox.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()){
                return false;
            }
            ItemStack playerMainCurItem = nearestPlayer.getItemBySlot(EquipmentSlot.MAINHAND);
            ItemStack playerOffCurItem = nearestPlayer.getItemBySlot(EquipmentSlot.OFFHAND);
            if (playerMainCurItem.isEmpty() && playerOffCurItem.isEmpty()){
                RevervoxMod.LOGGER.debug("[StridorvoxStealItemsGoal] player has no allowed item");
                return false;
            }
            return playerMainCurItem.is(StridorvoxEntity.ALLOWED_ITEMS);
        }
    }
    public void start() {
        RevervoxMod.LOGGER.debug("[StridorvoxStealItemsGoal] Stridorvox is stealing an item from a player");
        List<Player> list = stridorvox.level().getEntitiesOfClass(Player.class, stridorvox.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
        this.nearestPlayer = list.get(0);
        if (nearestPlayer.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()){
            stridorvox.setItemSlot(EquipmentSlot.MAINHAND, nearestPlayer.getItemBySlot(EquipmentSlot.OFFHAND));
            nearestPlayer.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        } else {
            stridorvox.setItemSlot(EquipmentSlot.MAINHAND, nearestPlayer.getItemBySlot(EquipmentSlot.MAINHAND));
            nearestPlayer.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        this.damageCount = 0;
        stridorvox.setSprinting(true);
        this.lastDamageTime = stridorvox.getLastHurtByMobTimestamp();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return stridorvox.distanceToSqr(nearestPlayer) < 20.0D || damageCount < 2;

    }

    public void tick() {
        if (stridorvox.getLastHurtByMobTimestamp() - lastDamageTime > 0){
            this.damageCount++;
            lastDamageTime = stridorvox.getLastHurtByMobTimestamp();
        }
        Vec3 vec3 = DefaultRandomPos.getPosAway(stridorvox, 16, 7, this.nearestPlayer.position());
        if (vec3 == null) {
            return;
        } else if (this.nearestPlayer.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.nearestPlayer.distanceToSqr(stridorvox)) {
            return;
        } else {
            this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
            if (this.path != null) {
                this.pathNav.moveTo(this.path, 0.7D);
            }
        }
    }

    @Override
    public void stop() {
        if (this.damageCount >= 2){
            ItemEntity itemEntity = new ItemEntity(stridorvox.level(), stridorvox.getX(), stridorvox.getY(), stridorvox.getZ(), stridorvox.getItemBySlot(EquipmentSlot.MAINHAND));
            stridorvox.level().addFreshEntity(itemEntity);
            stridorvox.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        RevervoxMod.LOGGER.debug("[StridorvoxStealItemsGoal] Stridorvox StealItemsGoal stop");
        this.damageCount = 0;
        stridorvox.setSprinting(false);
        super.stop();
    }
}