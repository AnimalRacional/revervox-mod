package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.ThingyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ThingyRenderer extends MobRenderer<ThingyEntity, ThingyModel<ThingyEntity>> {

    private static final float SHADOW_SIZE = 0.2f;

    public ThingyRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ThingyModel<>(pContext.bakeLayer(ModModelLayers.THINGY_LAYER)), SHADOW_SIZE);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull ThingyEntity pEntity) {
        return new ResourceLocation(RevervoxMod.MOD_ID, "textures/entity/thingy.png");
    }

    @Override
    public void render(@NotNull ThingyEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pMatrixStack,
                       @NotNull MultiBufferSource pBuffer, int pPackedLight) {


        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
