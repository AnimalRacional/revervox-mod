package dev.omialien.revervoxmod.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SummonFakeEntityCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("summonFakeEntity").requires((p) -> p.hasPermission(2)).then(
                    Commands.argument("target", GameProfileArgument.gameProfile()).executes((src) -> {
                        try{
                            GameProfile target = GameProfileArgument.getGameProfiles(src, "target").iterator().next();
                            Level level = src.getSource().getLevel();
                            Player player = level.getPlayerByUUID(target.getId());
                            if(player == null){ return 1; }
                            RevervoxMod.summonBatWave(player);
                        } catch(Exception e){
                            RevervoxMod.LOGGER.error("Error executing summonFakeEntity: {}\r\n{}", e.getMessage(), e.getStackTrace());
                        }

                        return 0;
                    })
                )
        );
    }


}
