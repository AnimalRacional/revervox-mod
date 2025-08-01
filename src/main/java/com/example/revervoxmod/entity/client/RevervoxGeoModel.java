package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RevervoxGeoModel extends DefaultedEntityGeoModel<RevervoxGeoEntity> {
    public RevervoxGeoModel() {
        super(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox"));
    }

}
