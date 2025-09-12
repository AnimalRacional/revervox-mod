package dev.omialien.revervoxmod.items;

import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.items.client.MegaphoneRenderer;
import dev.omialien.revervoxmod.registries.ParticleRegistry;
import dev.omialien.revervoxmod.voicechat.PlayerStateManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MegaphoneItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean usedItem = false;
    public MegaphoneItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) return;

        if (!level.isClientSide && !usedItem) {
            if ((!player.isCrouching() || !RevervoxModServerConfigs.CROUCH_PREVENTS_MEGAPHONE_BOOM.get()) && PlayerStateManager.isScreaming(player.getUUID())) {
                doSonicBoom(level, player);
                usedItem = true;
                player.getCooldowns().addCooldown(this, RevervoxModServerConfigs.MEGAPHONE_COOLDOWN.get() * 20);
            }
        }
    }

    @Override
    public void onStopUsing(@NotNull ItemStack stack, @NotNull LivingEntity entity, int count) {
        usedItem = false;
        super.onStopUsing(stack, entity, count);
    }

    private void doSonicBoom(Level level, Player player) {
        Vec3 vec3 = player.position().add(player.getAttachments().get(EntityAttachment.WARDEN_CHEST, 0, player.getYRot()));
        Vec3 playerFowardsPosition = player.getEyePosition().add(player.getLookAngle().scale(10));
        Vec3 vec31 = playerFowardsPosition.subtract(vec3);
        Vec3 vec32 = vec31.normalize();
        int i = Mth.floor(vec31.length()) + 7;

        List<LivingEntity> entitiesToHit = new ArrayList<>();

        for(int j = 1; j < i; ++j) {
            Vec3 vec33 = vec3.add(vec32.scale(j));
            //level.explode(player, vec33.x, vec33.y, vec33.z, 2, Level.ExplosionInteraction.BLOCK );
            ((ServerLevel) level).sendParticles(ParticleRegistry.REVERVOX_SONIC_BOOM_PARTICLES.get(), vec33.x, vec33.y, vec33.z, 1, 0.0, 0.0, 0.0, 0.0);
            AABB currentParticleAABB = new AABB(new BlockPos((int) vec33.x, (int) vec33.y, (int) vec33.z)).inflate(2.0D, 2.0D, 2.0D);
            List<LivingEntity> nearbyEntities = level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, null, currentParticleAABB);
            entitiesToHit.addAll(nearbyEntities);
        }
        for (LivingEntity entity : entitiesToHit) {
            if (entity != player && entity.hurt(level.damageSources().sonicBoom(player), 10.0F)) {
                double d1 = 0.5 * (1.0 - (entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double d0 = 2.5 * (1.0 - (entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                entity.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
                if (entity instanceof RevervoxGeoEntity revervox){
                    revervox.setStunned(true);
                }
            }
        }

        //TODO fazer knockback depender do RMS
        if (player instanceof ServerPlayer sp) {
            Vec3 look = player.getLookAngle();

            Vec3 recoil = look.reverse().normalize().scale(1.0);

            sp.push(recoil.x, recoil.y, recoil.z);
            sp.hurtMarked = true;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
        //TODO ver se da para fazer mais eficiente, versão com raycast aqui em baixo, apenas funciona se olhar diretamente para a entidade
        /*
        Vec3 viewVector = player.getViewVector(0.0F).normalize();
        Vec3 playerPos = player.getEyePosition();
        final int maxStopDistance = 200;
        Vec3 scaledView = viewVector.scale(maxStopDistance);
        AABB aabb = player.getBoundingBox().expandTowards(scaledView).inflate(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(player, playerPos, playerPos.add(scaledView), aabb,
                (filter) -> true, maxStopDistance);

        if (entityHitResult != null) {
            //level.explode(player, entityHitResult.getLocation().x(), entityHitResult.getLocation().y(), entityHitResult.getLocation().z(), 10, Level.ExplosionInteraction.TNT );
            Entity entity = entityHitResult.getEntity();
            RevervoxMod.LOGGER.debug("hit entity: {}", entity);
            if (entity instanceof LivingEntity) {
                if (entity.hurt(level.damageSources().sonicBoom(player), 10.0F)) {
                    double d1 = 0.5 * (1.0 - ((LivingEntity) entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    double d0 = 2.5 * (1.0 - ((LivingEntity) entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    entity.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
                }
            }
        }

         */
    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return RevervoxModServerConfigs.MEGAPHONE_COOLDOWN.get() * 20;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private MegaphoneRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new MegaphoneRenderer();

                return this.renderer;
            }
        });
    }
}
