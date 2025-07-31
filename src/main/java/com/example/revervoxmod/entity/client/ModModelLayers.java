package com.example.revervoxmod.entity.client;

import com.example.revervoxmod.RevervoxMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation THINGY_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "thingy_layer"), "main");
}
