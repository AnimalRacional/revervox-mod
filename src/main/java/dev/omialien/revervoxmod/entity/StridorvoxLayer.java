package dev.omialien.revervoxmod.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.omialien.revervoxmod.entity.custom.StridorvoxEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.util.RenderUtils;


public class StridorvoxLayer extends BlockAndItemGeoLayer<StridorvoxEntity> {
    public StridorvoxLayer(GeoRenderer<StridorvoxEntity> renderer) {
        super(renderer);
    }

    @Override
    protected ItemStack getStackForBone(GeoBone bone, StridorvoxEntity animatable) {
        return bone.getName().equals("Mouth") ? animatable.getItemBySlot(EquipmentSlot.MAINHAND) : null;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, StridorvoxEntity animatable,
                                      MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        RenderUtils.translateToPivotPoint(poseStack, bone);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));

        poseStack.translate(-0.2f, 2.7f, 0.3f);
        poseStack.scale(1.4f, 1.4f, 1.4f);

        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.mulPose(Axis.ZN.rotationDegrees(50f));

        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, StridorvoxEntity animatable) {
        return ItemDisplayContext.GROUND;
    }
}
