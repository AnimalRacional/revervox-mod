package dev.omialien.revervoxmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevervoxModServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_MAX_AUDIOS_TO_PLAY;
    public static final ForgeConfigSpec.ConfigValue<Float> REVERVOX_AFTER_SPEAK_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> RECORDING_LIMIT;
    public static final ForgeConfigSpec.ConfigValue<Integer> SILENCE_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SWORD_BONUS_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_TEETH_DROP_CHANCE;

    static {
        BUILDER.push("Server Configs for Revervox Mod");

        REVERVOX_MAX_AUDIOS_TO_PLAY = BUILDER.comment("Maximum audios that Revervox will play before disappearing").define("Revervox Max Audios", 20);
        REVERVOX_AFTER_SPEAK_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox will wait after speaking before being able to get angry").define("Revervox After Speaking Grace Period", 1.5f);
        RECORDING_LIMIT = BUILDER.comment("Maximum audios that will be saved in memory").define("Max Saved Recordings", 200);
        SILENCE_THRESHOLD = BUILDER.comment("Amplitude threshold to detect speech. Change this if you feel like Revervox notices you even when you're not speaking").define("Silence Threshold", 700);
        REVERVOX_BAT_SPAWN_CHANCE = BUILDER.comment("Chance of Revervox Bat spawning (1 in x)").define("Revervox Bat Spawn Chance", 10);
        REVERVOX_SWORD_BONUS_DAMAGE = BUILDER.comment("How much extra damage the revervox sword deals to revervox entities").define("Revervox Sword Bonus Damage", 7);
        REVERVOX_BAT_TEETH_DROP_CHANCE = BUILDER.comment("The chance of a revervox bat dropping a teeth on attack (1 in x)").define("Revervox Bat Teeth Drop Chance", 10);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
