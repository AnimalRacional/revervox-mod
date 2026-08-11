package dev.omialien.revervoxmod.sounds;

import dev.omialien.revervoxmod.registries.SoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class EchoDarkHeartbeat extends AbstractTickableSoundInstance {
    private final Player player;
    private static final float TARGET_PITCH = 2.0F;
    private static final float PITCH_FADE_SPEED = 0.01F;

    private boolean pitchFadingOut = false;
    protected EchoDarkHeartbeat(Player player){
        super(SoundRegistry.ECHO_DARK_HEARTBEAT.get(), SoundSource.AMBIENT, RandomSource.create());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.2F;
        this.pitch = 0.001F;
        this.relative = true;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    public void startPitchFadeOut() {
        this.pitchFadingOut = true;
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

        if (pitchFadingOut) {
            this.pitch -= PITCH_FADE_SPEED;
            if (this.pitch <= 0.0F) {
                this.volume = 0.0F;
                this.pitch = 0.0F;
                this.stop();
            }
        } else if (this.pitch < TARGET_PITCH) {
            this.pitch = Math.min(this.pitch + PITCH_FADE_SPEED, TARGET_PITCH);
        }
    }
}
