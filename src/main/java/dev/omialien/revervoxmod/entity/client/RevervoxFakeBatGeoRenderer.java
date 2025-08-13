package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.entity.custom.RevervoxFakeBatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RevervoxFakeBatGeoRenderer extends GeoEntityRenderer<RevervoxFakeBatEntity> {
    public RevervoxFakeBatGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RevervoxFakeBatGeoModel());
    }
}
