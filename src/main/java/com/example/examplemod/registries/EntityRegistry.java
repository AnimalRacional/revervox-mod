package com.example.examplemod.registries;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.custom.ThingyEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MOD_ID);
    
    public static final RegistryObject<EntityType<ThingyEntity>> THINGY =
            ENTITY_TYPES.register("thingy", () -> EntityType.Builder.of(ThingyEntity::new, MobCategory.CREATURE)
                    .sized(0.8F, 0.8F).build("thingy"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
