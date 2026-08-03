package dev.omialien.revervoxmod.items.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.TapeRecorderItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class TapeRecorderModel extends DefaultedItemGeoModel<TapeRecorderItem> {
    public TapeRecorderModel() {
        super(new ResourceLocation(RevervoxMod.MOD_ID, "tape_recorder_in_hand_on"));
    }

    @Override
    public DefaultedItemGeoModel<TapeRecorderItem> withAltModel(ResourceLocation altPath) {
        return super.withAltModel(altPath);
    }
}
