package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.networking.packets.SoundInstancePacket;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class RevervoxHurtByTargetGoal extends HurtByTargetGoal {
    private final RevervoxGeoEntity revervox;
    public RevervoxHurtByTargetGoal(RevervoxGeoEntity revervox) {
        super(revervox);
        this.revervox = revervox;
    }

    @Override
    public void start() {
        super.start();
        if (this.revervox.getTarget() instanceof Player) {
            revervox.level().playSound(null, revervox.getX(), revervox.getY(), revervox.getZ(), SoundRegistry.REVERVOX_ALERT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            PacketDistributor.sendToPlayersTrackingEntity(
                    this.mob,
                    new SoundInstancePacket(
                            this.mob.getId(),
                            SoundRegistry.REVERVOX_LOOP.get(),
                            SoundSource.HOSTILE,
                            true
                    )
            );
        }
    }
}
