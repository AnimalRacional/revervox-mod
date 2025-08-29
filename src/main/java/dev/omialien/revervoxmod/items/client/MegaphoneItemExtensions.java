package dev.omialien.revervoxmod.items.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.omialien.revervoxmod.items.MegaphoneItem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MegaphoneItemExtensions implements IClientItemExtensions {
    @Override
    public boolean applyForgeHandTransform(@NotNull PoseStack poseStack, @NotNull LocalPlayer player, @NotNull HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {

        if (itemInHand.getItem() instanceof MegaphoneItem) {
            if (player.isUsingItem()) {
                if (arm.equals(HumanoidArm.RIGHT)) {
                    poseStack.translate(-0.028, -0.029, -0.5);
                } else {
                    poseStack.translate(0.035, -0.04, -0.5);
                }
                poseStack.scale(0.75f, 0.75f, 0.75f);
                poseStack.mulPose(new Quaternionf().rotateAxis(90, new Vector3f(0.5f, 0, 0)));
            }
        }
        return IClientItemExtensions.DEFAULT.applyForgeHandTransform(poseStack, player, arm, itemInHand, partialTick, equipProcess, swingProcess);
    }

    @Override
    public HumanoidModel.@Nullable ArmPose getArmPose(@NotNull LivingEntity entityLiving, @NotNull InteractionHand hand, ItemStack itemStack) {
        if (itemStack.getItem() instanceof MegaphoneItem && FMLLoader.getDist() == Dist.CLIENT) {
            if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                return null;
            }
        }
        if (entityLiving.isUsingItem()) {
            return HumanoidModel.ArmPose.TOOT_HORN;
        }
        return null;
    }


}
