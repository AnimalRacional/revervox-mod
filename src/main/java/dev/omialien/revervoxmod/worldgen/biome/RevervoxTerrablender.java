package dev.omialien.revervoxmod.worldgen.biome;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class RevervoxTerrablender {
    public static void registerBiomes(){
        Regions.register(new RevervoxOverworldRegion(new ResourceLocation(RevervoxMod.MOD_ID, "overworld"), 7));
    }
}
