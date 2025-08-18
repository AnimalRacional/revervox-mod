package dev.omialien.revervoxmod.particle.custom;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(value = RevervoxMod.MOD_ID, dist = Dist.CLIENT)
public class RevervoxParticles extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float rotationSpeed;

    protected RevervoxParticles(ClientLevel pLevel, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(pLevel, xCoord, yCoord, zCoord, xd, yd, zd);

        this.spriteSet = spriteSet;

        this.lifetime = 18;

        float initialSpeed = 4.0f;
        double speed = Math.sqrt(xd * xd + yd * yd + zd * zd);
        if (speed > 0) {
            this.xd = (xd / speed) * (initialSpeed / 20.0);
            this.yd = (yd / speed) * (initialSpeed / 20.0);
            this.zd = (zd / speed) * (initialSpeed / 20.0);
        }

        this.quadSize = 0.5f;

        this.friction = 0.8f;

        this.roll =  (this.random.nextFloat() * 360 - 180) * Mth.DEG_TO_RAD;
        this.rotationSpeed = (float) (this.random.nextGaussian() * 0.1);

        // Dark gray tinting: [0.07059, 0.07059, 0.07059, 1] = RGB(18, 18, 18)
        this.rCol = 0.07059f;
        this.gCol = 0.07059f;
        this.bCol = 0.07059f;
        this.alpha = 1.0f;

        this.setSpriteFromAge(spriteSet);

        this.hasPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;


        if (this.onGround) {
            this.xd *= 0.6; // collision_drag: 0.4 means 60% velocity retained
            this.zd *= 0.6;
        }

        this.oRoll = this.roll;
        this.roll += this.rotationSpeed;

        this.setSpriteFromAge(this.spriteSet);

    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        // Make particles visible even in dark areas
        return 15728880; // Full brightness
    }


    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new RevervoxParticles(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}