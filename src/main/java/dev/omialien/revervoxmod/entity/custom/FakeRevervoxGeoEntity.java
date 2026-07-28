package dev.omialien.revervoxmod.entity.custom;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.util.PlayerVisibilityUtil;
import dev.omialien.revervoxmod.util.ViewUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class FakeRevervoxGeoEntity extends RevervoxGeoEntity{
    private static final int BEHIND_EVENT_DURATION_TICKS = 200;
    private long behindEventStartTime;
    private boolean behindEvent;
    private boolean playedSound;
    public FakeRevervoxGeoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.playedSound = false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Override
    protected void addBehaviourGoals() {

    }

    private void tickBehindEvent(){
        LivingEntity player = this.getTarget();
        if (player == null) return;
        if (ViewUtil.isInSight(player, this)){
            RevervoxMod.TASKS.schedule(() -> remove(RemovalReason.DISCARDED), 10);
            //TODO replace with custom sound?
            if (!this.playedSound) this.level().playSound(null, BlockPos.containing(this.position()), SoundEvents.AMBIENT_CAVE.value(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.playedSound = true;
            PlayerVisibilityUtil.restorePlayerVision((ServerPlayer) player);
            return;
        }

        if (this.level().getDayTime() - getBehindEventStartTime() > BEHIND_EVENT_DURATION_TICKS){
            this.remove(RemovalReason.DISCARDED);
            PlayerVisibilityUtil.restorePlayerVision((ServerPlayer) player);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (isBehindEvent()) {
                tickBehindEvent();
            }
        }
    }

    @Override
    protected void playHurtSound(DamageSource pSource) {

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        this.remove(RemovalReason.DISCARDED);
        return super.hurt(pSource, pAmount);
    }

    public boolean isBehindEvent() {
        return behindEvent;
    }

    public void setBehindEvent(boolean behindEvent) {
        this.behindEvent = behindEvent;
    }

    public long getBehindEventStartTime() {
        return behindEventStartTime;
    }

    public void setBehindEventStartTime(long behindEventStartTime) {
        this.behindEventStartTime = behindEventStartTime;
    }

}
