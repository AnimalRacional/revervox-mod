package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.entity.custom.StridorvoxEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StridorvoxGeoRenderer extends GeoEntityRenderer<StridorvoxEntity> {
    public StridorvoxGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StridorvoxGeoModel());
    }
}
