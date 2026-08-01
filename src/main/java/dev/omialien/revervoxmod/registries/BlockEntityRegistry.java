package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.blocks.VoiceRepeaterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {
    private static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RevervoxMod.MOD_ID);
    public static final RegistryObject<BlockEntityType<VoiceRepeaterBlockEntity>> TAPEBOX = REGISTRY.register(
            "tapebox",
            () -> BlockEntityType.Builder.of(
                    VoiceRepeaterBlockEntity::new, BlockRegistry.TAPEBOX.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
