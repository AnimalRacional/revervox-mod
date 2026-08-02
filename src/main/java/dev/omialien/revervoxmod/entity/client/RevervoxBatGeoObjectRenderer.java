package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class RevervoxBatGeoObjectRenderer extends GeoObjectRenderer<RevervoxBatGeoEntity> {
    public RevervoxBatGeoObjectRenderer() {
        super(new DefaultedEntityGeoModel<>(new ResourceLocation(RevervoxMod.MOD_ID, "revervox_bat")));
    }

}