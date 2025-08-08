package com.example.revervoxmod.entity.goals;

import com.example.revervoxmod.entity.custom.RevervoxGeoEntity;
import com.example.revervoxmod.networking.RevervoxPacketHandler;
import com.example.revervoxmod.networking.packets.AddSoundInstancePacket;
import com.example.revervoxmod.registries.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraftforge.network.PacketDistributor;

public class RevervoxHurtByTargetGoal extends HurtByTargetGoal {
    private final RevervoxGeoEntity revervox;
    public RevervoxHurtByTargetGoal(RevervoxGeoEntity revervox, Class<?>... pToIgnoreDamage) {
        super(revervox, pToIgnoreDamage);
        this.revervox = revervox;
    }

    @Override
    public void start() {
        if (!this.revervox.isAngry()){
            RevervoxPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.mob),
                    new AddSoundInstancePacket(this.mob.getId(), SoundRegistry.REVERVOX_ALERT.get(), SoundSource.HOSTILE));
        }
        super.start();
    }
}
