package dev.omialien.revervoxmod.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.client.RevervoxBatRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID, value = Dist.CLIENT)
public class RevervoxBatPeekEvents {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.getCameraType().isFirstPerson()) return;

        LocalPlayer player = mc.player;
        if (player == null || player.isInvisible() || !RevervoxBatRenderHelper.isActive(player)) return;

        PoseStack pose = event.getPoseStack();
        pose.pushPose();

        pose.mulPose(event.getCamera().rotation());
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        // -Z straight ahead, +Y up, +X right

        pose.translate(-0.5F, -0.7F, -0.4F);
        pose.scale(1F, 1F, 1F);

        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
        RevervoxBatRenderHelper.render(pose, buf, player, event.getPartialTick(),
                LightTexture.pack(15, 15));
        buf.endBatch();

        pose.popPose();
    }
}