package dev.omialien.revervoxmod.items;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThrownRevervoxBait extends ThrowableItemProjectile {
    private boolean hasDropped = false;
    public ThrownRevervoxBait(EntityType<? extends ThrownEgg> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownRevervoxBait(Level pLevel, LivingEntity pShooter) {
        super(EntityType.EGG, pShooter, pLevel);
    }

    public ThrownRevervoxBait(Level pLevel, double pX, double pY, double pZ) {
        super(EntityType.EGG, pX, pY, pZ, pLevel);
    }
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            double d0 = 0.08D;

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.EGG;
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);
        Vec3 hitLocation = pResult.getLocation();
        if (!this.level().isClientSide) {
            if (!hasDropped) {
                ItemEntity drop = new ItemEntity(this.level(), hitLocation.x, hitLocation.y, hitLocation.z, new ItemStack(Items.FERMENTED_SPIDER_EYE));
                if(this.getOwner() != null){
                    drop.setThrower(this.getOwner().getUUID());
                }
                this.level().addFreshEntity(drop);
                hasDropped = true;
            }
        }
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);
        pResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }
}
