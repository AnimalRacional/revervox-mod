package dev.omialien.revervoxmod.entity.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxFakeBatEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RevervoxFakeBatGeoModel extends DefaultedEntityGeoModel<RevervoxFakeBatEntity> {
    public RevervoxFakeBatGeoModel(){
        super(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "revervox_bat"), true);
    }
}
