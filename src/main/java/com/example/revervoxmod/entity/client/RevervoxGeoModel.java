package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RevervoxGeoModel extends GeoModel<RevervoxGeoEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "geo/revervox.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "textures/entity/revervox.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "animations/revervox.animation.json");
    @Override
    public ResourceLocation getModelResource(RevervoxGeoEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(RevervoxGeoEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(RevervoxGeoEntity animatable) {
        return animations;
    }
}
