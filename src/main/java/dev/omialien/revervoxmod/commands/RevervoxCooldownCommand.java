package dev.omialien.revervoxmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Optional;

public class RevervoxCooldownCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("revervoxCooldown").requires((p) -> p.hasPermission(Commands.LEVEL_GAMEMASTERS)).then(
                        Commands.literal("get").then(
                                Commands.argument("player", EntityArgument.players())
                                        .executes((src) -> {
                                            long gameTime = src.getSource().getLevel().getGameTime();
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(src, "player");
                                            for (ServerPlayer player : players) {
                                                Optional<Long> cd = RevervoxMod.COOLDOWN.getLastSpawn(player.getUUID());
                                                if (cd.isEmpty()) {
                                                    src.getSource().sendSuccess(
                                                            () -> Component.literal(player.getName() + " is not in cooldown"),
                                                            false
                                                    );
                                                } else {
                                                    long cdTime = cd.get();
                                                    if (gameTime >= cdTime + RevervoxModServerConfigs.REVERVOX_SPAWN_COOLDOWN.get()) {
                                                        src.getSource().sendSuccess(
                                                                () -> Component.literal(player.getName() + " is not in cooldown"),
                                                                false
                                                        );
                                                    } else {
                                                        long inCdFor = cdTime + RevervoxModServerConfigs.REVERVOX_SPAWN_COOLDOWN.get() - gameTime;
                                                        src.getSource().sendSuccess(
                                                                () -> Component.literal(player.getName() + ": " + inCdFor),
                                                                false
                                                        );
                                                    }
                                                }
                                            }
                                            return 1;
                                        })
                        )
                )
        );
    }
}