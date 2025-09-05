package dev.omialien.revervoxmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RevervoxModCommonConfigs {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.ConfigValue<Integer> RECORDING_LIMIT = BUILDER
            .define("maxSavedRecordings", 200);
    public static final ModConfigSpec.ConfigValue<Integer> MINIMUM_AUDIO_COUNT = BUILDER
            .define("minimumAudioCount", 50);
    public static final ModConfigSpec SPEC = BUILDER.build();
}
