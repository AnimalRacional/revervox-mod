package dev.omialien.revervoxmod.items.client;

import dev.omialien.revervoxmod.items.MegaphoneItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MegaphoneRenderer extends GeoItemRenderer<MegaphoneItem> {
    public MegaphoneRenderer() {
        super(new MegaphoneModel());
    }
}
