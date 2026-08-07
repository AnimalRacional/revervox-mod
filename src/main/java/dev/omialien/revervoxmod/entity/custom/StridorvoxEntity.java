package dev.omialien.revervoxmod.entity.custom;

import dev.omialien.revervoxmod.entity.ai.MMEntityMoveHelper;
import dev.omialien.revervoxmod.entity.goals.FleeOnScreamGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StridorvoxEntity extends Monster implements GeoEntity, HearingEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

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
    protected void registerGoals() {
        int goalPrio = 0;
        this.goalSelector.addGoal(goalPrio++, new FloatGoal(this));
        this.goalSelector.addGoal(goalPrio++, new FleeOnScreamGoal(this, 0.5d, 0.7d));
        this.goalSelector.addGoal(goalPrio++, new MeleeAttackGoal(this, 0.7d, true));
        this.goalSelector.addGoal(goalPrio++, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(goalPrio++, new RandomLookAroundGoal(this));

        int targetPrio = 0;
        this.targetSelector.addGoal(goalPrio++, new NearestAttackableTargetGoal<Player>(this, Player.class, true, true));
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
