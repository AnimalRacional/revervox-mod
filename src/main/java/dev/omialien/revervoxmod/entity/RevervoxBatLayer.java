package dev.omialien.revervoxmod.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.omialien.revervoxmod.entity.client.RevervoxBatRenderHelper;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class RevervoxBatLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public RevervoxBatLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        if (player.isInvisible()) return;
        pose.pushPose();
        this.getParentModel().head.translateAndRotate(pose);
        pose.scale(-1.0F, -1.0F, 1.0F);
        RevervoxBatRenderHelper.render(pose, buffer, player, partialTick, packedLight);
        pose.popPose();
    }
}
