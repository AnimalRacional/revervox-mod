package dev.omialien.revervoxmod.voicechat;

import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import dev.omialien.revervoxmod.RevervoxMod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//TODO fazer isto generico: o PlayerStateManager tem diferentes PlayerStates e depois quando quiser saber o estado do player,
// basta chamar o PlayerStateManager.getPlayerState(player.getUUID(), PlayerStateType.SCREAMING);
public class PlayerStateManager {
    private static final Map<UUID, PlayerState> PLAYER_STATES = new ConcurrentHashMap<>();

    public static void createState(UUID uuid) {
        if(PLAYER_STATES.containsKey(uuid)){
            RevervoxMod.LOGGER.error("Tried to add already-existing state! {}", uuid);
            return;
        }
        PLAYER_STATES.put(uuid, new PlayerState(uuid));
    }

    public static void removeState(UUID uuid){
        if(!PLAYER_STATES.containsKey(uuid)) {
            RevervoxMod.LOGGER.error("Tried to remove non-existing state! {}", uuid);
            return;
        }
        PLAYER_STATES.remove(uuid);
    }

    public static PlayerState getState(UUID uuid) { return PLAYER_STATES.get(uuid); }

    public static boolean isScreaming(UUID uuid) {
        return getState(uuid).isScreaming();
    }

    public static void addScreamingPlayer(UUID uuid) {
        getState(uuid).setScreaming(true);
    }

    public static void removeScreamingPlayer(UUID uuid) {
        getState(uuid).setScreaming(false);
    }

    public static boolean isUsingMegaphone(UUID uuid) {
        return getState(uuid).isUsingMegaphone();
    }

    public static void addUsingMegaphone(UUID uuid) {
        getState(uuid).setMegaphone(true);
    }

    public static void removeUsingMegaphone(UUID uuid) {
        getState(uuid).setMegaphone(false);
    }

    public static OpusDecoder getPlayerDecoder(UUID uuid) {
        return getState(uuid).getDecoder();
    }
    public static OpusEncoder getPlayerEncoder(UUID uuid) {
        return getState(uuid).getEncoder();
    }
}
