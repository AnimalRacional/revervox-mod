package dev.omialien.revervoxmod.items.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.MegaphoneItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MegaphoneModel extends DefaultedItemGeoModel<MegaphoneItem> {
    public MegaphoneModel() {
        super(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, "megaphone_in_hand"));
    }

}
