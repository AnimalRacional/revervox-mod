package dev.omialien.revervoxmod.items.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.omialien.revervoxmod.items.TapeRecorderItem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TapeRecorderItemExtensions implements IClientItemExtensions {
    private GeoItemRenderer<TapeRecorderItem> renderer;
    private final boolean on;

    public TapeRecorderItemExtensions(boolean on) {
        this.on = on;
    }

    @Override
    public boolean applyForgeHandTransform(@NotNull PoseStack poseStack, @NotNull LocalPlayer player, @NotNull HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
        if (itemInHand.getItem() instanceof TapeRecorderItem && player.isUsingItem()) {
            float tx = -0.025f;
            float ty = -0.029f;
            float tz = -0.22f;

            float pitchDeg = -54.25f + 14f;
            float yawDeg   = -20.5f;
            float rollDeg  = 0f;

            if (arm == HumanoidArm.LEFT) {
                poseStack.translate(tx, ty, tz);
                poseStack.scale(0.75f, 0.75f, 0.75f);
                poseStack.mulPose(new Quaternionf().rotationXYZ(
                        (float) Math.toRadians(pitchDeg),
                        (float) Math.toRadians(yawDeg),
                        (float) Math.toRadians(rollDeg)
                ));
            } else { // RIGHT
                poseStack.translate(-tx, ty, tz);
                poseStack.scale(0.75f, 0.75f, 0.75f);
                poseStack.mulPose(new Quaternionf().rotationXYZ(
                        (float) Math.toRadians(pitchDeg),
                        (float) Math.toRadians(-yawDeg),
                        (float) Math.toRadians(-rollDeg)
                ));
            }
        }
        return IClientItemExtensions.DEFAULT.applyForgeHandTransform(poseStack, player, arm, itemInHand, partialTick, equipProcess, swingProcess);
    }

    @Override
    public HumanoidModel.@Nullable ArmPose getArmPose(@NotNull LivingEntity entityLiving, @NotNull InteractionHand hand, ItemStack itemStack) {
        if (itemStack.getItem() instanceof TapeRecorderItem && FMLLoader.getDist() == Dist.CLIENT) {
            if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON && Minecraft.getInstance().cameraEntity != null && entityLiving.is(Minecraft.getInstance().cameraEntity)) {
                return null;
            }
        }
        if (entityLiving.isUsingItem()) {
            return HumanoidModel.ArmPose.TOOT_HORN;
        }
        return null;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (this.renderer == null)
            if (this.on){
                this.renderer = new TapeRecorderRenderer();
            } else {
                this.renderer = new TapeRecorderOffRenderer();
            }

        return this.renderer;
    }
}
