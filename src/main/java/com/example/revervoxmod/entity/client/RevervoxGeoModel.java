package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RevervoxGeoModel extends GeoModel<RevervoxGeoEntity> {
    //Models
    private static final ResourceLocation MODEL_A = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "geo/entity/revervox.geo.json");
    private static final ResourceLocation MODEL_B = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "geo/entity/revervox_bat.geo.json");

    // Textures
    private static final ResourceLocation TEXTURE_A = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "textures/entity/revervox.png");
    private static final ResourceLocation TEXTURE_B = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "textures/entity/revervox_bat.png");

    // Animations
    private static final ResourceLocation ANIM_A = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "animations/entity/revervox.animation.json");
    private static final ResourceLocation ANIM_B = ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "animations/entity/revervox_bat.animation.json");

    @Override
    public ResourceLocation getModelResource(RevervoxGeoEntity animatable) {
        return animatable.getModelType() == 1 ? MODEL_B : MODEL_A;
    }

    @Override
    public ResourceLocation getTextureResource(RevervoxGeoEntity animatable) {
        return animatable.getModelType() == 1 ? TEXTURE_B : TEXTURE_A;
    }

    @Override
    public ResourceLocation getAnimationResource(RevervoxGeoEntity animatable) {
        return animatable.getModelType() == 1 ? ANIM_B : ANIM_A;
    }
}












































































































