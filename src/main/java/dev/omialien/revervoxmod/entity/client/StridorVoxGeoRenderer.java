package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.entity.custom.StridorVoxEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StridorVoxGeoRenderer extends GeoEntityRenderer<StridorVoxEntity> {
    public StridorVoxGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StridorVoxGeoModel());
    }
}
