package dev.omialien.revervoxmod.voicechat;

import dev.omialien.voicechat_recording.configs.RecordingCommonConfig;

public class AudioUtil {
    public static short[] applyRadioEffect(short[] pcmBE, double gain) {
        float[] samples = new float[pcmBE.length];
        for (int i = 0; i < pcmBE.length; i++) {
            samples[i] = pcmBE[i] / 32768.0f;
        }

        float[] filtered = bandPassFilter(samples, 300.0, 3500.0, 48000);

        short[] out = new short[filtered.length];
        for (int i = 0; i < filtered.length; i++) {
            float v = (float) (filtered[i] * gain);
            v = Math.max(-1.0f, Math.min(1.0f, v)); // prevent clipping
            out[i] = (short) (v * 32767);
        }

        return out;
    }

    private static float[] bandPassFilter(float[] input, double lowCut, double highCut, int sampleRate) {
        int filterSize = 101; // longer = sharper filter
        float[] filter = new float[filterSize];

        double nyquist = sampleRate / 2.0;
        double low = lowCut / nyquist;
        double high = highCut / nyquist;

        for (int i = 0; i < filterSize; i++) {
            int m = i - filterSize / 2;
            if (m == 0) {
                filter[i] = (float) (2 * (high - low));
            } else {
                filter[i] = (float) ((Math.sin(2 * Math.PI * high * m) - Math.sin(2 * Math.PI * low * m)) / (Math.PI * m));
            }
            filter[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (filterSize - 1));
        }

        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++) {
            double acc = 0;
            for (int j = 0; j < filterSize; j++) {
                int idx = i - j;
                if (idx >= 0) acc += input[idx] * filter[j];
            }
            output[i] = (float) acc;
        }

        return output;
    }

    public static double calculateRMS(short[] audio){
        int start;
        for(start = 0; start < audio.length && Math.abs(audio[start]) < (Integer) RecordingCommonConfig.SILENCE_THRESHOLD.get(); ++start) {
        }

        int end;
        for(end = audio.length - 1; end > start && Math.abs(audio[end]) < (Integer)RecordingCommonConfig.SILENCE_THRESHOLD.get(); --end) {
        }

        int activeSamples = end - start + 1;
        long sumSquares = 0L;

        for(int i = start; i <= end; ++i) {
            int sample = audio[i];
            sumSquares += (long)(sample * sample);
        }

        return Math.sqrt((double)sumSquares / (double)activeSamples);
    }
}
