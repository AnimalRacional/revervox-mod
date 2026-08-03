package dev.omialien.revervoxmod.items.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.items.TapeRecorderItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TapeRecorderOffRenderer extends GeoItemRenderer<TapeRecorderItem> {
    public TapeRecorderOffRenderer() {
        super(new TapeRecorderModel().withAltModel(new ResourceLocation(RevervoxMod.MOD_ID, "tape_recorder_in_hand_off")));
    }


}
