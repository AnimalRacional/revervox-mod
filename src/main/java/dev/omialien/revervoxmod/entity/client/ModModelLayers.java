package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation THINGY_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "thingy_layer"), "main");
}
