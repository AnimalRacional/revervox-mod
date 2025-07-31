package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.ThingyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ThingyRenderer extends MobRenderer<ThingyEntity, ThingyModel<ThingyEntity>> {

    private static final float SHADOW_SIZE = 0.2f;

    public ThingyRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ThingyModel<>(pContext.bakeLayer(ModModelLayers.THINGY_LAYER)), SHADOW_SIZE);
    }

    @Override
    public ResourceLocation getTextureLocation(ThingyEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "textures/entity/thingy.png");
    }

    @Override
    public void render(ThingyEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                       MultiBufferSource pBuffer, int pPackedLight) {


        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
