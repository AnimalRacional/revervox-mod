package dev.omialien.revervoxmod.datagen;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.worldgen.biome.RevervoxBiomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RevervoxWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, RevervoxBiomes::bootstrap);

    public RevervoxWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(RevervoxMod.MOD_ID));
    }
}
