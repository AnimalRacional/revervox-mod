package dev.omialien.revervoxmod.entity;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class RevervoxCooldownManager {
    private final HashMap<UUID, Long> cooldowns;
    public RevervoxCooldownManager() {
        cooldowns = new HashMap<>();
    }

    public boolean isInCooldown(UUID player, long currentTime) {
        Long lastSpawn = cooldowns.get(player);
        if (lastSpawn == null) {
            return false;
        }
        if (currentTime >= lastSpawn + RevervoxModServerConfigs.REVERVOX_SPAWN_COOLDOWN.get()) {
            cooldowns.remove(player);
            return false;
        }
        return true;
    }

    public void markSpawn(UUID player, long currentTime) {
        RevervoxMod.LOGGER.debug("marking cooldown for {} at {}", player, currentTime);
        cooldowns.put(player, currentTime);
    }

    public void markSpawn(Iterator<UUID> players, long currentTime) {
        while (players.hasNext()) {
            UUID player = players.next();
            markSpawn(player, currentTime);
        }
    }

    public Optional<Long> getLastSpawn(UUID player) {
        return Optional.ofNullable(cooldowns.get(player));
    }
}
