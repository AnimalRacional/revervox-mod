package com.example.examplemod.entity.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.custom.RevervoxGeoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RevervoxGeoModel extends GeoModel<RevervoxGeoEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "geo/revervox.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/entity/revervox.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "animations/revervox.animation.json");
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
