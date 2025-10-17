package dev.omialien.revervoxmod.entity.goals;

import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import dev.omialien.revervoxmod.networking.RevervoxPacketHandler;
import dev.omialien.revervoxmod.networking.packets.AddSoundInstancePacket;
import dev.omialien.revervoxmod.registries.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class RevervoxHurtByTargetGoal extends HurtByTargetGoal {
    public RevervoxHurtByTargetGoal(RevervoxGeoEntity revervox) {
        super(revervox);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !(this.mob.getTarget() instanceof Player);
    }

    @Override
    public void start() {
        super.start();
        if (this.mob.getTarget() instanceof Player) {
            mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundRegistry.REVERVOX_ALERT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            RevervoxPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.mob),
                    new AddSoundInstancePacket(this.mob.getId(), SoundRegistry.REVERVOX_LOOP.get(), SoundSource.HOSTILE, true));
        }
    }
}
