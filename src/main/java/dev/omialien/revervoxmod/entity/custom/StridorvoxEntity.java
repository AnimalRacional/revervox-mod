package dev.omialien.revervoxmod.entity.custom;

import dev.omialien.revervoxmod.entity.ai.MMEntityMoveHelper;
import dev.omialien.revervoxmod.entity.goals.FleeOnScreamGoal;
import dev.omialien.revervoxmod.entity.goals.RangeMeleeAttackGoal;
import dev.omialien.revervoxmod.entity.goals.StalkGoal;
import dev.omialien.revervoxmod.entity.goals.StridorvoxStealItemsGoal;
import dev.omialien.revervoxmod.registries.RevervoxTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

public class StridorvoxEntity extends Monster implements GeoEntity, HearingEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public static final Predicate<Holder<Item>> ALLOWED_ITEMS = (item) ->
            item.is(ItemTags.SWORDS) || item.is(ItemTags.PICKAXES)
                    || item.is(ItemTags.AXES) || item.is(ItemTags.SHOVELS)
                    || item.is(ItemTags.PIGLIN_LOVED) || item.is(RevervoxTags.Items.AUDIO_ON_KILL);

    public StridorvoxEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        moveControl = new MMEntityMoveHelper(this, 90);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0d)
                .add(Attributes.ATTACK_DAMAGE, 4.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.5d)
                .add(Attributes.FOLLOW_RANGE, 32.0d)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0d)
                .add(Attributes.ATTACK_SPEED, 1.0d);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItemBySlot(EquipmentSlot.MAINHAND));
        this.level().addFreshEntity(itemEntity);
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        super.die(pDamageSource);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, pLevel);
        navigation.setMaxVisitedNodesMultiplier(4);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        int goalPrio = 0;
        this.goalSelector.addGoal(goalPrio++, new FloatGoal(this));
        this.goalSelector.addGoal(goalPrio++, new StridorvoxStealItemsGoal(this));
        this.goalSelector.addGoal(goalPrio++, new FleeOnScreamGoal(this, 0.5d, 0.7d));
        this.goalSelector.addGoal(goalPrio++, new RangeMeleeAttackGoal(this, 0.7d, true, 10, 30));
        this.goalSelector.addGoal(goalPrio++, new StalkGoal(this));
        this.goalSelector.addGoal(goalPrio++, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(goalPrio++, new RandomLookAroundGoal(this));

        int targetPrio = 0;
        this.targetSelector.addGoal(targetPrio++, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
    }

    @Override
    public boolean isSpeakingAtMe(Player player) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                DefaultAnimations.genericWalkRunIdleController(this).transitionLength(5)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
