package com.example.revervoxmod.registries;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.entity.custom.ThingyEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final float THINGY_HITBOX_SIZE = 0.8F;
    public static final float REVERVOX_HITBOX_WIDTH = 2F;
    public static final float REVERVOX_HITBOX_HEIGHT = 4F;

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RevervoxMod.MOD_ID);
    public static final RegistryObject<EntityType<ThingyEntity>> THINGY =
            ENTITY_TYPES.register("thingy", () -> EntityType.Builder.of(ThingyEntity::new, MobCategory.CREATURE)
                    .sized(THINGY_HITBOX_SIZE, THINGY_HITBOX_SIZE).build("thingy"));
    public static final RegistryObject<EntityType<RevervoxGeoEntity>> REVERVOX_GEO =
            ENTITY_TYPES.register("revervox_geo", () -> EntityType.Builder.of(RevervoxGeoEntity::new, MobCategory.MONSTER)
                    .sized(REVERVOX_HITBOX_WIDTH, REVERVOX_HITBOX_HEIGHT).build("revervox_geo"));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
