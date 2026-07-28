package dev.omialien.revervoxmod.util;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PlayerVisibilityUtil {

    public static void isolatePlayer(ServerPlayer serverPlayer) {
        serverPlayer.addTag("revervox_behind_event_target");

        AABB area = serverPlayer.getBoundingBox().inflate(50.0D);
        List<ServerPlayer> nearbyPlayers = serverPlayer.serverLevel().getPlayers(p ->
                p != serverPlayer && area.contains(p.position())
        );

        ChunkMap chunkMap = serverPlayer.serverLevel().getChunkSource().chunkMap;

        for (ServerPlayer otherPlayer : nearbyPlayers) {
            ChunkMap.TrackedEntity trackedEntity = chunkMap.entityMap.get(otherPlayer.getId());
            if (trackedEntity != null) {
                trackedEntity.removePlayer(serverPlayer);
            }
        }
    }

    public static void restorePlayerVision(ServerPlayer serverPlayer) {
        serverPlayer.removeTag("revervox_behind_event_target");

        List<ServerPlayer> allPlayers = serverPlayer.serverLevel().players();
        ChunkMap chunkMap = serverPlayer.serverLevel().getChunkSource().chunkMap;

        for (ServerPlayer otherPlayer : allPlayers) {
            if (otherPlayer == serverPlayer) continue;

            ChunkMap.TrackedEntity trackedEntity = chunkMap.entityMap.get(otherPlayer.getId());
            if (trackedEntity != null) {
                trackedEntity.removePlayer(serverPlayer);
                trackedEntity.updatePlayer(serverPlayer);
            }
        }
    }
}
