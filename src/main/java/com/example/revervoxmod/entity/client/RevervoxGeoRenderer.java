package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RevervoxGeoRenderer extends GeoEntityRenderer<RevervoxGeoEntity> {
    public RevervoxGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new RevervoxGeoModel());
    }

}
