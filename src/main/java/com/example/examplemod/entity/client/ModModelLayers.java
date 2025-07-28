package com.example.examplemod.entity.client;

import com.example.examplemod.ExampleMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation THINGY_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "thingy_layer"), "main");
    public static final ModelLayerLocation REVERVOX_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "revervox_layer"), "main");

}
