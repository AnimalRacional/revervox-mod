package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RevervoxBatGeoModel extends DefaultedEntityGeoModel<RevervoxBatGeoEntity> {
    public RevervoxBatGeoModel() {
        super(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox_bat"), true);
    }
}
