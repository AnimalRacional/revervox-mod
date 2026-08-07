package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.StridorvoxEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StridorvoxGeoModel extends DefaultedEntityGeoModel<StridorvoxEntity> {
    public StridorvoxGeoModel() {
        super(new ResourceLocation(RevervoxMod.MOD_ID, "stridorvox"));
    }
}
