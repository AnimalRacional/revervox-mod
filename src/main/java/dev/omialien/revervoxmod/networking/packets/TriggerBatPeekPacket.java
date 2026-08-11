package dev.omialien.revervoxmod.networking.packets;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.entity.client.RevervoxBatRenderHelper;
import dev.omialien.revervoxmod.sounds.EchoDarkSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TriggerBatPeekPacket {
    private final UUID player;

    public TriggerBatPeekPacket(UUID player) {
        this.player = player;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.player);
    }

    public static TriggerBatPeekPacket decode(FriendlyByteBuf buf) {
        return new TriggerBatPeekPacket(buf.readUUID());
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        RevervoxMod.LOGGER.debug("received peek packet");
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) { return; }
            net.minecraft.world.entity.player.Player plr = Minecraft.getInstance().level.getPlayerByUUID(this.player);
            if (plr instanceof AbstractClientPlayer acp) {
                RevervoxMod.LOGGER.info("activating RevervoxBatRenderHelper on client");
                RevervoxBatRenderHelper.activate(acp, 60);
                EchoDarkSoundHandler.resetHeartbeat();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
