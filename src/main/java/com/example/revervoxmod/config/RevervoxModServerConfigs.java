package com.example.revervoxmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevervoxModServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_MAX_AUDIOS_TO_PLAY;
    public static final ForgeConfigSpec.ConfigValue<Float> REVERVOX_AFTER_SPEAK_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> RECORDING_LIMIT;
    public static final ForgeConfigSpec.ConfigValue<Integer> SILENCE_THRESHOLD;
    static {
        BUILDER.push("Server Configs for Revervox Mod");

        REVERVOX_MAX_AUDIOS_TO_PLAY = BUILDER.comment("Maximum audios that Revervox will play before disappearing").define("Revervox Max Audios", 20);
        REVERVOX_AFTER_SPEAK_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox will wait after speaking before being able to get angry").define("Revervox After Speaking Grace Period", 1.5f);
        RECORDING_LIMIT = BUILDER.comment("Maximum audios that will be saved in memory").define("Max Saved Recordings", 200);
        SILENCE_THRESHOLD = BUILDER.comment("Amplitude threshold to detect speech. Change this if you feel like Revervox notices you even when you're not speaking").define("Silence Threshold", 700);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
