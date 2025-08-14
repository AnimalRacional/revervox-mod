package dev.omialien.revervoxmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevervoxModServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_MAX_AUDIOS_TO_PLAY;
    // TODO grace periods não aparecem na config screen, talvez por serem floats
    public static final ForgeConfigSpec.ConfigValue<Float> REVERVOX_AFTER_SPEAK_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Float> REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> RECORDING_LIMIT;
    public static final ForgeConfigSpec.ConfigValue<Integer> SILENCE_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SWORD_BONUS_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_TOOTH_DROP_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> AUDIO_READER_THREAD_COUNT;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Float> FAKE_BAT_EVENT_CHANCE;

    static {
        BUILDER.push("Server Configs for Revervox Mod");

        REVERVOX_MAX_AUDIOS_TO_PLAY = BUILDER.comment("Maximum audios that Revervox will play before disappearing").define("Revervox Max Audios", 20);
        REVERVOX_AFTER_SPEAK_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox will wait after speaking before being able to get angry").define("Revervox After Speaking Grace Period", 1.5f);
        REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox Bat will wait after spawn before being able to get angry").define("Revervox Bat After Spawning Grace Period", 0.5f);
        REVERVOX_SPAWN_CHANCE = BUILDER.comment("Minimum distance between every Revervox").define("Revervox Spawn Chance", 100);
        RECORDING_LIMIT = BUILDER.comment("Maximum audios that will be saved in memory").define("Max Saved Recordings", 200);
        SILENCE_THRESHOLD = BUILDER.comment("Amplitude threshold to detect speech. Change this if you feel like Revervox notices you even when you're not speaking").define("Silence Threshold", 700);
        REVERVOX_BAT_SPAWN_CHANCE = BUILDER.comment("Chance of Revervox Bat spawning (1 in x)").define("Revervox Bat Spawn Chance", 5);
        REVERVOX_SWORD_BONUS_DAMAGE = BUILDER.comment("How much extra damage the revervox sword deals to revervox entities").define("Revervox Sword Bonus Damage", 7);
        REVERVOX_BAT_TOOTH_DROP_CHANCE = BUILDER.comment("The chance of a revervox bat dropping its tooth on attack (1 in x)").define("Revervox Bat Tooth Drop Chance", 10);
        AUDIO_READER_THREAD_COUNT = BUILDER.comment("How many threads are used to read the saved audios from a player that joins the server. Lower this if a player joining the server lags, and raise it if it doesn't but audio reading is too slow.").define("Audio Reading Thread Count", 4);
        FAKE_BAT_EVENT_CHANCE = BUILDER.comment("Chance of fake bat event occuring. Higher is less likely, lower is more likely").define("Fake Bat Event Chance", 1.0f);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
