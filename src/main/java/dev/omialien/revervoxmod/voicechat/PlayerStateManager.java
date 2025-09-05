package dev.omialien.revervoxmod.voicechat;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import dev.omialien.voicechat_recording.VoiceChatRecording;
import oshi.util.tuples.Pair;

import java.util.*;

//TODO fazer isto generico: o PlayerStateManager tem diferentes PlayerStates e depois quando quiser saber o estado do player,
// basta chamar o PlayerStateManager.getPlayerState(player.getUUID(), PlayerStateType.SCREAMING);
public class PlayerStateManager {
    private static final Set<UUID> screamingPlayers = new HashSet<>();
    private static final Map<UUID, Pair<OpusDecoder, OpusEncoder>> playerCoders = new HashMap<>();

    public static boolean isScreaming(UUID uuid) {
        return screamingPlayers.contains(uuid);
    }

    public static void addScreamingPlayer(UUID uuid) {
        if (screamingPlayers.contains(uuid)) return;
        screamingPlayers.add(uuid);
    }

    public static void removeScreamingPlayer(UUID uuid) {
        if (!screamingPlayers.contains(uuid)) return;
        screamingPlayers.remove(uuid);
    }

    public static OpusDecoder getPlayerDecoder(UUID uuid) {
        return playerCoders.get(uuid).getA();
    }
    public static OpusEncoder getPlayerEncoder(UUID uuid) {
        return playerCoders.get(uuid).getB();
    }

    public static void addPlayerCoders(UUID uuid) {
        if (VoiceChatRecording.vcApi instanceof VoicechatServerApi api) {
            OpusDecoder decoder = api.createDecoder();
            OpusEncoder encoder = api.createEncoder();
            playerCoders.put(uuid, new Pair<>(decoder, encoder));
        }
    }

    public static void removePlayerCoders(UUID uuid) {
        playerCoders.remove(uuid);
    }
}
