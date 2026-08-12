package dev.omialien.revervoxmod.blocks.client;

import dev.omialien.revervoxmod.blocks.NightmareChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class NightmareChestRenderer extends GeoBlockRenderer<NightmareChestBlockEntity> {
    public NightmareChestRenderer(BlockEntityRendererProvider.Context context) {
        super(new NightmareChestModel());
    }
}
