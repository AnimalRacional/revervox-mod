package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.custom.RevervoxBatGeoEntity;
import dev.omialien.revervoxmod.entity.custom.RevervoxFakeBatEntity;
import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.entity.custom.ThingyEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    public static final float THINGY_HITBOX_SIZE = 0.8F;
    public static final float REVERVOX_HITBOX_WIDTH = 1.3F;
    public static final float REVERVOX_HITBOX_HEIGHT = 2.9F;

    private static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, RevervoxMod.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ThingyEntity>> THINGY =
            REGISTRY.register("thingy", () -> EntityType.Builder.of(ThingyEntity::new, MobCategory.CREATURE)
                    .sized(THINGY_HITBOX_SIZE, THINGY_HITBOX_SIZE).build("thingy"));
    public static final DeferredHolder<EntityType<?>, EntityType<RevervoxGeoEntity>> REVERVOX =
            REGISTRY.register("revervox", () -> EntityType.Builder.of(RevervoxGeoEntity::new, MobCategory.MONSTER)
                    .sized(REVERVOX_HITBOX_WIDTH, REVERVOX_HITBOX_HEIGHT).build("revervox"));
    public static final DeferredHolder<EntityType<?>, EntityType<RevervoxBatGeoEntity>> REVERVOX_BAT =
            REGISTRY.register("revervox_bat", () -> EntityType.Builder.of(RevervoxBatGeoEntity::new, MobCategory.AMBIENT)
                    .sized(0.5F, 1).build("revervox_bat"));
    public static final DeferredHolder<EntityType<?>, EntityType<RevervoxFakeBatEntity>> REVERVOX_FAKE_BAT =
            REGISTRY.register("revervox_fake_bat", () -> EntityType.Builder.of(RevervoxFakeBatEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 1).build("revervox_bat"));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
