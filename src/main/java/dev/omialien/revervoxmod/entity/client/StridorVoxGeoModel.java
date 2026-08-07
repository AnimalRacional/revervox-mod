package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.StridorVoxEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StridorVoxGeoModel extends DefaultedEntityGeoModel<StridorVoxEntity> {
    public StridorVoxGeoModel() {
        super(new ResourceLocation(RevervoxMod.MOD_ID, "stridorvox"), true);
    }
}
