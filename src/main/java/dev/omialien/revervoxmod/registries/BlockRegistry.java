package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.blocks.EchoDarkGrassBlock;
import dev.omialien.revervoxmod.blocks.VoiceRepeaterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, RevervoxMod.MOD_ID);
    public static final RegistryObject<VoiceRepeaterBlock> TAPEBOX = REGISTRY.register(
            "tapebox",
            () -> new VoiceRepeaterBlock(BlockBehaviour.Properties.of().strength(1.0f, 0.2f).randomTicks())
    );
    public static final RegistryObject<Block> ECHO_DARK_SURFACE_BLOCK = REGISTRY.register(
            "echo_dark_surface_block",
            () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.SCULK).strength(1.0f, 0.2f).randomTicks())
    );
    public static final RegistryObject<Block> ECHO_DARK_GRASS = REGISTRY.register(
            "echo_dark_grass",
            () -> new EchoDarkGrassBlock(BlockBehaviour.Properties.of().replaceable().noCollission().mapColor(MapColor.PLANT).instabreak().sound(SoundType.SCULK).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY))
    );
    public static final RegistryObject<Block> GUANO_BLOCK = REGISTRY.register(
            "guano_block",
            () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.0f, 0.2f).randomTicks())
    );

    public static final RegistryObject<Block> GUANO = REGISTRY.register(
            "guano",
            () -> new CarpetBlock(BlockBehaviour.Properties.of().sound(SoundType.MUD).strength(1.0f, 0.2f).randomTicks())
    );

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
