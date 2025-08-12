package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RevervoxBatGeoRenderer extends GeoEntityRenderer<RevervoxBatGeoEntity> {
    public RevervoxBatGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RevervoxBatGeoModel());
    }
}
