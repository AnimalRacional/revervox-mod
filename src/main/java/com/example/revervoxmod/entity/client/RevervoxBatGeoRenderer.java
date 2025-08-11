package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RevervoxBatGeoRenderer extends GeoEntityRenderer<RevervoxBatGeoEntity> {
    public RevervoxBatGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RevervoxBatGeoModel());
    }
}
