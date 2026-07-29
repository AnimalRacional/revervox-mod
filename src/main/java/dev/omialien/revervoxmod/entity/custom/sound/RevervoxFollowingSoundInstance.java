package dev.omialien.revervoxmod.entity.custom.sound;

import dev.omialien.revervoxmod.entity.custom.RevervoxGeoEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class RevervoxFollowingSoundInstance extends AbstractTickableSoundInstance {
    protected final RevervoxGeoEntity entity;
    public RevervoxFollowingSoundInstance(RevervoxGeoEntity entity, SoundEvent soundEvent, SoundSource soundSource) {
        super(soundEvent, soundSource, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.delay = 0;
        this.volume = 0.8F;
        this.x = ((float)entity.getX());
        this.y = ((float)entity.getY());
        this.z = ((float)entity.getZ());
    }

    @Override
    public void tick() {
        if (this.entity.isRemoved()) {
            this.stop();
        } else if (entity.shouldStopPlaying()) {
            this.stop();
        } else {
            this.x = ((float)this.entity.getX());
            this.y = ((float)this.entity.getY());
            this.z = ((float)this.entity.getZ());
        }
    }
}
