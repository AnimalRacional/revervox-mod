package dev.omialien.revervoxmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import dev.omialien.revervoxmod.registries.EntityRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.Map;
import java.util.WeakHashMap;

public class RevervoxBatRenderHelper {
    private static final RevervoxBatGeoObjectRenderer RENDERER = new RevervoxBatGeoObjectRenderer();
    private static final Map<AbstractClientPlayer, RevervoxBatGeoEntity> DUMMIES = new WeakHashMap<>();
    private static final Map<AbstractClientPlayer, Long> ACTIVE_UNTIL = new WeakHashMap<>();

    public static void activate(AbstractClientPlayer player, int durationTicks) {
        ACTIVE_UNTIL.put(player, player.level().getGameTime() + durationTicks);
        trigger(player, "Stare");
    }

    public static boolean isActive(AbstractClientPlayer player) {
        Long until = ACTIVE_UNTIL.get(player);
        return until != null && player.level().getGameTime() < until;
    }

    public static RevervoxBatGeoEntity getDummy(AbstractClientPlayer player) {
        RevervoxBatGeoEntity d = DUMMIES.computeIfAbsent(player,
                p -> new RevervoxBatGeoEntity(EntityRegistry.REVERVOX_BAT.get(), p.level()));
        d.tickCount = player.tickCount;
        return d;
    }

    public static void trigger(AbstractClientPlayer player, String animName) {
        RevervoxBatGeoEntity dummy = getDummy(player);
        AnimationController<?> controller = dummy.getAnimatableInstanceCache()
                .getManagerForId(dummy.getId())
                .getAnimationControllers()
                .get("Stare");
        if (controller != null) {
            controller.forceAnimationReset();
            controller.tryTriggerAnimation(animName);
        }
    }

    public static void render(PoseStack pose, MultiBufferSource buffer,
                              AbstractClientPlayer player, float partialTick, int light) {
        pose.translate(0.0F, 0F, 0.0F);
        pose.scale(1F, 1F, 1F);
        RENDERER.render(pose, getDummy(player), buffer, null, null, light);
    }
}