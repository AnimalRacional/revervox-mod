package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.blocks.VoiceRepeaterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, RevervoxMod.MOD_ID);
    public static final RegistryObject<VoiceRepeaterBlock> VOICE_REPEATER_BLOCK = REGISTRY.register(
            "voice_repeater",
            () -> new VoiceRepeaterBlock(BlockBehaviour.Properties.of().strength(1.0f, 0.2f).randomTicks())
    );

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
