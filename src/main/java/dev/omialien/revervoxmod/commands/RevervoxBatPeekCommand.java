package dev.omialien.revervoxmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.networking.packets.TriggerBatPeekPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;

public class RevervoxBatPeekCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("batPeek").requires((p) -> p.hasPermission(2)).then(
                        Commands.argument("target", EntityArgument.players()).executes((src) -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(src, "target");
                            for (ServerPlayer player : players) {
                                RevervoxPacketHandler.INSTANCE.send(
                                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                                        new TriggerBatPeekPacket(player.getUUID())
                                );
                            }
                            return 0;
                        })
                )
        );
    }
}
