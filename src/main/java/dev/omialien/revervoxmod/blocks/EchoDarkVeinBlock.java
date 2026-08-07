package dev.omialien.revervoxmod.blocks;

import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;

public class EchoDarkVeinBlock extends MultifaceBlock {
    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public EchoDarkVeinBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return spreader;
    }
}
