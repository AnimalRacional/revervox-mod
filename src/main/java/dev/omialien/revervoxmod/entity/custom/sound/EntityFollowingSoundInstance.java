package dev.omialien.revervoxmod.entity.custom.sound;


import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityFollowingSoundInstance extends AbstractTickableSoundInstance {
    private final LivingEntity entity;
    public EntityFollowingSoundInstance(LivingEntity entity, SoundEvent soundEvent, SoundSource soundSource) {
        super(soundEvent, soundSource, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.looping = false;
        this.delay = 0;
        this.volume = 1.0F;
        this.x = ((float)entity.getX());
        this.y = ((float)entity.getY());
        this.z = ((float)entity.getZ());
    }

    public boolean canPlaySound() {
        return !this.entity.isSilent();
    }

    @Override
    public void tick() {
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            this.x = ((float)this.entity.getX());
            this.y = ((float)this.entity.getY());
            this.z = ((float)this.entity.getZ());
        }

    }
}
