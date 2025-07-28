package com.example.examplemod.entity.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.custom.RevervoxEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;


public class RevervoxRenderer extends MobRenderer<RevervoxEntity, RevervoxModel<RevervoxEntity>> {
    private static final float SHADOW_SIZE = 1f;

    public RevervoxRenderer(EntityRendererProvider.Context pcontext) {
        super(pcontext, new RevervoxModel<>(pcontext.bakeLayer(ModModelLayers.REVERVOX_LAYER)), SHADOW_SIZE);
    }

    @Override
    public ResourceLocation getTextureLocation(RevervoxEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/entity/revervox.png");
    }

    @Override
    public void render(RevervoxEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                       MultiBufferSource pBuffer, int pPackedLight) {


        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
