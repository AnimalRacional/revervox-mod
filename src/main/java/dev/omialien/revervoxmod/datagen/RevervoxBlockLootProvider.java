package dev.omialien.revervoxmod.datagen;

import dev.omialien.revervoxmod.registries.BlockRegistry;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RevervoxBlockLootProvider extends BlockLootSubProvider {

    public RevervoxBlockLootProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(BlockRegistry.TAPEBOX.get());
        this.dropWhenSilkTouch(BlockRegistry.ECHO_DARK_SURFACE_BLOCK.get());
        this.dropSelf(BlockRegistry.NIGHTMARE_CHEST.get());
        this.add(BlockRegistry.ECHO_DARK_VEIN_BLOCK.get(), (b) -> this.createMultifaceBlockDrops(b, HAS_SILK_TOUCH));
        this.add(BlockRegistry.ECHO_DARK_GRASS.get(), BlockLootSubProvider::createShearsOnlyDrop);
        this.add(BlockRegistry.ECHO_DARK_PLANT.get(), BlockLootSubProvider::createShearsOnlyDrop);
        this.dropSelf(BlockRegistry.GUANO_BLOCK.get());
        this.dropSelf(BlockRegistry.GUANO.get());
        this.dropSelf(BlockRegistry.ECHO_DARK_BRICKS.get());
        this.dropSelf(BlockRegistry.ECHO_DARK_BRICKS_STAIRS.get());
        this.add(BlockRegistry.ECHO_DARK_BRICKS_SLAB.get(), this::createSlabItemTable);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BlockRegistry.REGISTRY.getEntries().stream().flatMap(RegistryObject::stream)::iterator;
    }
}
