package com.example.revervoxmod.commands;

import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;
import com.example.revervoxmod.voicechat.audio.AudioPlayer;
import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.voicechat.RecordedPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

public class NearestEntityPlayVoiceCommand {
    public static final int PERMISSION_LEVEL = 2;
    private static final int CHANNEL_DISTANCE = 20;
    public static final int bbX = 5;
    public static final int bbY = 5;
    public static final int bbZ = 5;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("playVoice").requires((src) -> {
                    return src.hasPermission(PERMISSION_LEVEL);
                }).then(
                        Commands.argument("player", GameProfileArgument.gameProfile())
                                .then(Commands.argument("index", IntegerArgumentType.integer())
                                        .executes(NearestEntityPlayVoiceCommand::runCommand)
                                        .then(Commands.argument("entity", EntityArgument.entities())
                                                .executes(NearestEntityPlayVoiceCommand::runCommandEntity)))
                )
        );
    }

    public static int runCommandEntity(CommandContext<CommandSourceStack> cmdSrc) throws CommandSyntaxException {
        if (RevervoxMod.vcApi instanceof VoicechatServerApi api){

            Collection<? extends Entity> entities = EntityArgument.getEntities(cmdSrc, "entity");

            for(Entity nearestEntity : entities){
                RevervoxMod.LOGGER.debug("Entity: " + nearestEntity.getName());
                String category = RevervoxVoicechatPlugin.REVERVOX_CATEGORY;
                Collection<GameProfile> targets = GameProfileArgument.getGameProfiles(cmdSrc, "player");
                int idx = IntegerArgumentType.getInteger(cmdSrc, "index");
                for (GameProfile target : targets) {
                    UUID channelID = UUID.randomUUID();
                    EntityAudioChannel channel = createChannel(api, channelID, category, nearestEntity);
                    RevervoxMod.LOGGER.debug("Created new channel: " + channel);
                    new AudioPlayer(getAudioPath(target.getId(), idx), api, channel).start();
                    RevervoxMod.LOGGER.debug("silent: " + nearestEntity.isSilent());
                }
            }
            return 1;
        }
        return 0;
    }

    public static int runCommand(CommandContext<CommandSourceStack> cmdSrc) {
        try {
            if (RevervoxMod.vcApi instanceof VoicechatServerApi api) {
                ServerLevel level = cmdSrc.getSource().getLevel();
                Vec3 srcPos = cmdSrc.getSource().getPosition();
                AABB aabb = new AABB(srcPos.x + bbX, srcPos.y + bbY, srcPos.z + bbZ,
                        srcPos.x - bbX, srcPos.y - bbY, srcPos.z - bbZ);
                Entity nearestEntity = level.getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT,
                        null, srcPos.x, srcPos.y, srcPos.z, aabb);
                if (nearestEntity != null) {
                    RevervoxMod.LOGGER.debug("Entity: " + nearestEntity.getName());
                    String category = RevervoxVoicechatPlugin.REVERVOX_CATEGORY;
                    Collection<GameProfile> targets = GameProfileArgument.getGameProfiles(cmdSrc, "player");
                    int idx = IntegerArgumentType.getInteger(cmdSrc, "index");
                    for (GameProfile target : targets) {
                        UUID channelID = UUID.randomUUID();
                        EntityAudioChannel channel = createChannel(api, channelID, category, nearestEntity);
                        RevervoxMod.LOGGER.debug("Created new channel: " + channel);
                        new AudioPlayer(getAudioPath(target.getId(), idx), api, channel).start();
                        RevervoxMod.LOGGER.debug("silent: " + nearestEntity.isSilent());
                    }

                } else {
                    RevervoxMod.LOGGER.warn("No Entity Found");
                }

                return 1;
            }
        } catch(Exception e){
            RevervoxMod.LOGGER.error("Error on playvoice: {}", e.getMessage());
        }
        return 0;
    }

    private static Path getAudioPath(UUID uuid, int index){
        Path audioPath = RecordedPlayer.audiosPath.resolve(uuid.toString()).resolve(uuid + "-" + index + ".pcm");
        RevervoxMod.LOGGER.debug("Audio Path: " + audioPath);
        return audioPath;
    }


    private static EntityAudioChannel createChannel(VoicechatServerApi api, UUID channelID, String category, Entity nearestEntity) {
        EntityAudioChannel channel = api.createEntityAudioChannel(channelID, api.fromEntity(nearestEntity));
        if (channel == null) {
            RevervoxMod.LOGGER.error("Couldn't create channel");
            return null;
        }
        channel.setCategory(category); // The category of the audio channel
        channel.setDistance(NearestEntityPlayVoiceCommand.CHANNEL_DISTANCE); // The distance in which the audio channel can be heard
        return channel;
    }

}
