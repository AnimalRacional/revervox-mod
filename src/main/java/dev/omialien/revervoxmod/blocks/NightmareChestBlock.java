    package dev.omialien.revervoxmod.blocks;

    import dev.omialien.revervoxmod.RevervoxMod;
    import dev.omialien.revervoxmod.registries.BlockEntityRegistry;
    import dev.omialien.revervoxmod.worldgen.dimension.NightmareInstances;
    import dev.omialien.revervoxmod.worldgen.dimension.RevervoxDimensions;
    import dev.omialien.revervoxmod.worldgen.portal.RevervoxTeleporter;
    import net.minecraft.core.BlockPos;
    import net.minecraft.network.chat.Component;
    import net.minecraft.server.MinecraftServer;
    import net.minecraft.server.level.ServerLevel;
    import net.minecraft.server.level.ServerPlayer;
    import net.minecraft.world.InteractionHand;
    import net.minecraft.world.InteractionResult;
    import net.minecraft.world.entity.Entity;
    import net.minecraft.world.entity.player.Player;
    import net.minecraft.world.level.Level;
    import net.minecraft.world.level.block.ChestBlock;
    import net.minecraft.world.level.block.RenderShape;
    import net.minecraft.world.level.block.entity.BlockEntity;
    import net.minecraft.world.level.block.state.BlockBehaviour;
    import net.minecraft.world.level.block.state.BlockState;
    import net.minecraft.world.phys.BlockHitResult;
    import org.jetbrains.annotations.NotNull;
    import org.jetbrains.annotations.Nullable;

    import java.util.UUID;

    public class NightmareChestBlock extends ChestBlock{

        public NightmareChestBlock(BlockBehaviour.Properties properties) {
            super(properties, BlockEntityRegistry.NIGHTMARE_CHEST::get);
        }

        @Override
        public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
            return new NightmareChestBlockEntity(pPos, pState);
        }

        @Override
        public RenderShape getRenderShape(BlockState state) {
            return RenderShape.ENTITYBLOCK_ANIMATED;
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                     InteractionHand hand, BlockHitResult hit) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            if (!serverPlayer.canChangeDimensions() || serverPlayer.isPassenger()) return InteractionResult.CONSUME;

            if (!(level.getBlockEntity(pos) instanceof NightmareChestBlockEntity chest)) {
                return super.use(state, level, pos, player, hand, hit);
            }
            if (!chest.isNightmare()) {
                return super.use(state, level, pos, player, hand, hit);
            }

            NightmareInstances nightmareInstances = NightmareInstances.get(serverPlayer.server);

            UUID id = serverPlayer.getUUID();
            MinecraftServer server = serverPlayer.server;
            RevervoxMod.TASKS.schedule(() -> {
                ServerPlayer p = server.getPlayerList().getPlayer(id);
                if (p != null) nightmareInstances.sendHome(p);
            }, 600);

            if (nightmareInstances.isNightmare(serverPlayer.level().dimension())) {
                nightmareInstances.sendHome(serverPlayer);
                return InteractionResult.CONSUME;
            }

            nightmareInstances.setReturn(serverPlayer);
            int slot = nightmareInstances.acquire(id);

            chest.triggerAnim("Chest", "nightmare_open");

            ServerLevel target;
            if (slot < 0) {
                target = server.getLevel(RevervoxDimensions.PIT_LEVEL_KEY);
                if (target == null) return InteractionResult.CONSUME;
            } else {
                target = server.getLevel(RevervoxDimensions.HOUSE_LEVELS.get(slot));
                if (target == null || !NightmareInstances.buildHouse(serverPlayer, target)) {
                    nightmareInstances.release(server, id);
                    serverPlayer.sendSystemMessage(Component.literal("No bed"));
                    return InteractionResult.CONSUME;
                }
            }

            BlockPos spawn = slot < 0 ? RevervoxDimensions.PIT_SPAWN : RevervoxDimensions.houseSpawn();
            Entity moved = serverPlayer.changeDimension(target,
                    new RevervoxTeleporter(spawn, serverPlayer.getYRot(), serverPlayer.getXRot()));

            if (moved == null) {
                if (slot >= 0) nightmareInstances.release(server, id);
                return InteractionResult.CONSUME;
            }

            RevervoxMod.TASKS.schedule(() -> {
                ServerPlayer p = server.getPlayerList().getPlayer(id);
                if (p != null && nightmareInstances.isNightmare(p.level().dimension())) nightmareInstances.sendHome(p);
            }, 600);

            return InteractionResult.CONSUME;
        }
    }
