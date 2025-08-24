package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RevervoxBatGeoModel extends DefaultedEntityGeoModel<RevervoxBatGeoEntity> {
    public RevervoxBatGeoModel() {
        super(new ResourceLocation(RevervoxMod.MOD_ID, "revervox_bat"), true);
    }
}
