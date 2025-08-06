package com.example.revervoxmod.voicechat.audio;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Arrays;

public class AudioManipulator {
    private static final int SAMPLE_RATE = 48000;

    public static short[] shiftPitch(short[] inputPcm, float pitchFactor) {
        throw new NotImplementedException("Pitch shifter not implemented");
    }


    /**
     * Adds a simple reverb using delayed echo taps.
     */
    public static short[] addReverb(short[] input, float decay, int delayMs, int repeats) {
        if (decay <= 0 || decay >= 1) throw new IllegalArgumentException("Decay must be between 0 and 1");
        if (delayMs <= 0 || repeats <= 0) throw new IllegalArgumentException("Delay and repeats must be > 0");

        int delaySamples = (SAMPLE_RATE * delayMs) / 1000;
        short[] output = Arrays.copyOf(input, input.length);

        for (int r = 1; r <= repeats; r++) {
            int offset = delaySamples * r;
            float currentDecay = (float)Math.pow(decay, r);

            for (int i = 0; i < input.length - offset; i++) {
                int delayedIndex = i + offset;
                int mixed = output[delayedIndex] + (int)(input[i] * currentDecay);
                // Clip to 16-bit signed
                output[delayedIndex] = (short)Math.max(Math.min(mixed, Short.MAX_VALUE), Short.MIN_VALUE);
            }
        }

        return output;
    }
}
