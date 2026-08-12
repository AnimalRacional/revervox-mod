package dev.omialien.revervoxmod.blocks.client;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.blocks.NightmareChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class NightmareChestModel extends DefaultedBlockGeoModel<NightmareChestBlockEntity> {

    public NightmareChestModel() {
        super(new ResourceLocation(RevervoxMod.MOD_ID, "nightmare_chest"));
    }
}
