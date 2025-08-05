package com.example.revervoxmod.commands;

import com.example.revervoxmod.RevervoxMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ScheduleLogCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("schedulelog")
                        .requires((src) -> {
                            return src.hasPermission(2);
                        })
                        .then(Commands.argument("ticks", IntegerArgumentType.integer())
                                .then(Commands.argument("text", StringArgumentType.greedyString())
                                        .executes((src) -> {
                                            String text = StringArgumentType.getString(src, "text");
                                            RevervoxMod.TASKS.schedule(() -> {
                                                RevervoxMod.LOGGER.info(text);
                                            }, IntegerArgumentType.getInteger(src, "ticks"));
                                            return 1;
                                })))
        );
    }
}
