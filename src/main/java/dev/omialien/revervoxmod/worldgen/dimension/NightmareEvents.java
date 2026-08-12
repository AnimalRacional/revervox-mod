package dev.omialien.revervoxmod.worldgen.dimension;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RevervoxMod.MOD_ID)
public class NightmareEvents {

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer serverPlayer) {
            NightmareInstances.get(serverPlayer.server).release(serverPlayer.server, serverPlayer.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer serverPlayer)) return;
        NightmareInstances mgr = NightmareInstances.get(serverPlayer.server);
        if (mgr.isNightmare(serverPlayer.level().dimension())) mgr.sendHome(serverPlayer);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity() instanceof ServerPlayer serverPlayer) {
            NightmareInstances.get(serverPlayer.server).release(serverPlayer.server, serverPlayer.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent e) {
        for (ResourceKey<Level> key : RevervoxDimensions.HOUSE_LEVELS) {
            ServerLevel l = e.getServer().getLevel(key);
            if (l != null) NightmareInstances.wipe(l);
        }
    }
}
