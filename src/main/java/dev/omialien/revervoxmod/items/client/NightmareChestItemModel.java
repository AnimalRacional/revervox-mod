package dev.omialien.revervoxmod.items.client;

import dev.omialien.revervoxmod.items.NightmareChestItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NightmareChestItemModel extends GeoModel<NightmareChestItem> {

    @Override
    public ResourceLocation getModelResource(NightmareChestItem animatable) {
        return new ResourceLocation("revervox_mod", "geo/block/nightmare_chest.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NightmareChestItem animatable) {
        return new ResourceLocation("revervox_mod", "textures/block/nightmare_chest.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NightmareChestItem animatable) {
        return new ResourceLocation("revervox_mod", "animations/nightmare_chest.animation.json");
    }
}