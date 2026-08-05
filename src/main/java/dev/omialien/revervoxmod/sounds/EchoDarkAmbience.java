package dev.omialien.revervoxmod.sounds;

import dev.omialien.revervoxmod.registries.SoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class EchoDarkAmbience extends AbstractTickableSoundInstance {
    private final Player player;
    private static final float TARGET_VOLUME = 1.0F;
    private static final float FADE_SPEED = 0.02F;

    private boolean fadingOut = false;


    public EchoDarkAmbience(Player player) {
        super(SoundRegistry.ECHO_DARK_AMBIENCE.get(), SoundSource.AMBIENT, RandomSource.create());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001F;
        this.pitch = 1.0F;
        this.relative = true;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    public void startFadeOut() {
        this.fadingOut = true;
    }

    @Override
    public void tick() {
        if (!player.isAlive()) {
            this.stop();
            return;
        }

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();

        if (fadingOut) {
            this.volume -= FADE_SPEED;
            if (this.volume <= 0.0F) {
                this.volume = 0.0F;
                this.stop();
            }
        } else if (this.volume < TARGET_VOLUME) {
            this.volume = Math.min(this.volume + FADE_SPEED, TARGET_VOLUME);
        }
    }
}
