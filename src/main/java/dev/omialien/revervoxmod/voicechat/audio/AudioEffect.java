package dev.omialien.revervoxmod.voicechat.audio;

import java.util.Random;

public class AudioEffect {
    private float pitchFactor;
    private float reverbDecay;
    private int reverbDelayMs;
    private int reverbRepeats;
    private boolean pitchEnabled, reverbEnabled;
    private static final int SAMPLE_RATE = 48000;
    private boolean robotEnabled;
    private float robotLfoFreq;


    public AudioEffect(){
        this.pitchFactor = 1;
        this.reverbDecay = 0;
        this.reverbDelayMs = 0;
        this.reverbRepeats = 0;
        this.pitchEnabled = false;
        this.reverbEnabled = false;
    }

    public AudioEffect changePitch(float pitchFactor) {
        this.pitchFactor = pitchFactor;
        this.pitchEnabled = true;
        return this;
    }

    public AudioEffect makeReverb(float decay, int delayMs, int repeats) {
        this.reverbDecay = decay;
        this.reverbDelayMs = delayMs;
        this.reverbRepeats = repeats;
        this.reverbEnabled = true;
        return this;
    }

    public AudioEffect makeRobot(float lfoFreqHz) {
        this.robotEnabled = true;
        this.robotLfoFreq = lfoFreqHz;
        return this;
    }

    public AudioEffect addRandomEffects() {
        Random random = new Random();

        boolean added = false;

        if (random.nextBoolean()) {
            changePitch(0.7f);
            added = true;
        }

        if (random.nextBoolean()) {
            makeReverb(0.5f, 160, 2);
            added = true;
        }

        if (random.nextBoolean()) {
            makeRobot(30f);
            added = true;
        }

        if (!added) {
            int forced = random.nextInt(3);
            switch (forced) {
                case 0 -> changePitch(0.7f);
                case 1 -> makeReverb(0.5f, 160, 2);
                case 2 -> makeRobot(30f);
            }
        }

        return this;
    }

    public short[] applyEffects(short[] pcm) {
        if (pitchEnabled) pcm = changePitch(pcm, pitchFactor);
        if (reverbEnabled) pcm = addReverb(pcm, reverbDecay, reverbDelayMs, reverbRepeats);
        if (robotEnabled) pcm = robotize(pcm, robotLfoFreq);
        return pcm;
    }

    public static short[] changePitch(short[] pcm, float pitchFactor) {
        if (pitchFactor <= 0) throw new IllegalArgumentException("Pitch factor must be > 0");

        int newLength = (int)(pcm.length / pitchFactor);
        short[] result = new short[newLength];

        for (int i = 0; i < newLength; i++) {
            float srcIndex = i * pitchFactor;
            int index = (int) srcIndex;
            float frac = srcIndex - index;

            if (index + 1 < pcm.length) {
                result[i] = (short)((1 - frac) * pcm[index] + frac * pcm[index + 1]);
            } else {
                result[i] = pcm[index];
            }
        }

        return result;
    }

    public static short[] addReverb(short[] input, float decay, int delayMs, int repeats) {
        if (decay <= 0 || decay >= 1) throw new IllegalArgumentException("Decay must be between 0 and 1");
        if (delayMs <= 0 || repeats <= 0) throw new IllegalArgumentException("Delay and repeats must be > 0");

        int delaySamples = (SAMPLE_RATE * delayMs) / 1000;

        int totalLength = input.length + delaySamples * repeats;
        short[] output = new short[totalLength];

        System.arraycopy(input, 0, output, 0, input.length);

        for (int r = 1; r <= repeats; r++) {
            int offset = delaySamples * r;
            float currentDecay = (float) Math.pow(decay, r);

            for (int i = 0; i < input.length; i++) {
                int delayedIndex = i + offset;
                if (delayedIndex >= output.length) break;
                int mixed = output[delayedIndex] + (int) (input[i] * currentDecay);
                output[delayedIndex] = (short) Math.max(Math.min(mixed, Short.MAX_VALUE), Short.MIN_VALUE);
            }
        }

        return output;
    }

    public static short[] robotize(short[] pcm, float lfoFreqHz) {
        if (lfoFreqHz <= 0) throw new IllegalArgumentException("LFO frequency must be > 0");

        short[] output = new short[pcm.length];
        double lfoPhase = 0;
        double lfoIncrement = 2.0 * Math.PI * lfoFreqHz / SAMPLE_RATE;

        for (int i = 0; i < pcm.length; i++) {
            double modulator = Math.cos(lfoPhase);
            lfoPhase += lfoIncrement;
            if (lfoPhase >= 2.0 * Math.PI) lfoPhase -= 2.0 * Math.PI;

            int sample = (int) (pcm[i] * modulator);
            if (sample > Short.MAX_VALUE) sample = Short.MAX_VALUE;
            else if (sample < Short.MIN_VALUE) sample = Short.MIN_VALUE;

            output[i] = (short) sample;
        }

        return output;
    }

}
