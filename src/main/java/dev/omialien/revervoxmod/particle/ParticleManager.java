package dev.omialien.revervoxmod.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;

import java.util.Random;

public class ParticleManager {
    public static void addParticlesAroundSelf(ParticleOptions pParticleOption, Entity entity) {
        addParticlesAroundSelf(pParticleOption, 1.0, entity);
    }

    public static void addParticlesAroundSelf(ParticleOptions pParticleOption, double radius, Entity entity) {
        addParticlesAroundSelf(pParticleOption, radius, 30, entity);
    }

    public static void addParticlesAroundSelf(ParticleOptions pParticleOption, double radius, int particleCount, Entity entity) {
        for(int i = 0; i < particleCount; i++) {
            double offsetX = (new Random().nextDouble() - 0.5) * 2.0 * radius;
            double offsetY = (new Random().nextDouble() - 0.5) * 2.0 * radius;
            double offsetZ = (new Random().nextDouble() - 0.5) * 2.0 * radius;

            double particleX = entity.getX() + offsetX;
            double particleY = entity.getY() + 1.0 + offsetY;
            double particleZ = entity.getZ() + offsetZ;

            double velocityScale = radius * 0.1;
            double velX = (new Random().nextDouble() - 0.5) * velocityScale;
            double velY = (new Random().nextDouble() - 0.5) * velocityScale;
            double velZ = (new Random().nextDouble() - 0.5) * velocityScale;

            entity.level().addParticle(pParticleOption, particleX, particleY, particleZ, velX, velY, velZ);
        }
    }

}
