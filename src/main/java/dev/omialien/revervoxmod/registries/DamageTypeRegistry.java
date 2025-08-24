package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> REVERVOX_BONUS = register("revervox_bonus");

    private static ResourceKey<DamageType> register(String name){
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(RevervoxMod.MOD_ID, name));
    }
}
