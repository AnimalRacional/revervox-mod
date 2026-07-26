package dev.omialien.revervoxmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevervoxModServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> RECORDING_LIMIT;
    public static final ForgeConfigSpec.ConfigValue<Integer> MINIMUM_AUDIO_COUNT;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_MAX_AUDIOS;
    public static final ForgeConfigSpec.ConfigValue<Double> REVERVOX_AFTER_SPEAK_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Double> REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SWORD_BONUS_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_TOOTH_DROP_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> FAKE_BAT_EVENT_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_FAKE_BAT_EVENT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REVERVOX_BREAKS_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REVERVOX_BREAKS_NONSOLID;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REVERVOX_ABOVE_GROUND;
    public static final ForgeConfigSpec.BooleanValue REVERVOX_SONIC_BOOM;
    public static final ForgeConfigSpec.IntValue REVERVOX_SONIC_BOOM_COOLDOWN;
    public static final ForgeConfigSpec.IntValue REVERVOX_SONIC_BOOM_SECS_OUT_OF_SIGHT;
    public static final ForgeConfigSpec.IntValue REVERVOX_SONIC_BOOM_RANGE;
    public static final ForgeConfigSpec.IntValue MEGAPHONE_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue CROUCH_PREVENTS_MEGAPHONE_BOOM;

    static {
        BUILDER.push("Server Configs for Revervox Mod");

        RECORDING_LIMIT = BUILDER.comment("The maximum amount of player recordings that can be saved").define("Maximum Saved Recordings", 200);
        MINIMUM_AUDIO_COUNT = BUILDER.comment("The minimum amount of audios before they start being deleted").define("Minimum Audio Count", 50);

        REVERVOX_MAX_AUDIOS = BUILDER.comment("Maximum audios that Revervox will play before disappearing").define("Revervox Max Audios", 12);
        REVERVOX_AFTER_SPEAK_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox will wait after speaking before being able to get angry").define("Revervox After Speaking Grace Period", 1.5d);
        REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox Bat will wait after spawn before being able to get angry").define("Revervox Bat After Spawning Grace Period", 0.5d);
        REVERVOX_SPAWN_CHANCE = BUILDER.comment("Minimum distance between every Revervox").define("Revervox Spawn Chance", 100);
        REVERVOX_BAT_SPAWN_CHANCE = BUILDER.comment("Chance of Revervox Bat spawning (1 in x)").define("Revervox Bat Spawn Chance", 5);
        REVERVOX_SWORD_BONUS_DAMAGE = BUILDER.comment("How much extra damage the revervox sword deals to revervox entities").define("Revervox Sword Bonus Damage", 7);
        REVERVOX_BAT_TOOTH_DROP_CHANCE = BUILDER.comment("The chance of a revervox bat dropping its tooth on attack (1 in x)").define("Revervox Bat Tooth Drop Chance", 10);
        FAKE_BAT_EVENT_CHANCE = BUILDER.comment("Chance of fake bat event occuring. Higher is less likely, lower is more likely").define("Fake Bat Event Chance", 1.0d);
        ENABLE_FAKE_BAT_EVENT = BUILDER.comment("Whether the fake bat event will occur").define("Enable Fake Bat Event", true);
        REVERVOX_BREAKS_BLOCKS = BUILDER.comment("Whether Revervox breaks blocks to get to you").define("Revervox Block Breaking", false);
        REVERVOX_BREAKS_NONSOLID = BUILDER.comment("Whether Revervox breaks non-solid blocks such as torches and leaves").define("Revervox breaks non-solid blocks", true);
        REVERVOX_ABOVE_GROUND = BUILDER.comment("Whether Revervox spawns above ground").define("Revervox above ground", false);

        REVERVOX_SONIC_BOOM = BUILDER.comment("Whether Revervox uses the Sonic Boom ability").define("Revervox Sonic Boom", true);
        REVERVOX_SONIC_BOOM_COOLDOWN = BUILDER.comment("Cooldown (in seconds) between Revervox Sonic Booms").defineInRange("Revervox Sonic Boom Cooldown", 35, 1, 100);
        REVERVOX_SONIC_BOOM_SECS_OUT_OF_SIGHT = BUILDER.comment("Time (in seconds) that Revervox needs to be out of sight before being able to use a sonic boom").defineInRange("Revervox Sonic Boom Secs out of Sight", 5, 1, 100);
        REVERVOX_SONIC_BOOM_RANGE = BUILDER.comment("Range (in blocks) of Revervox's Sonic Boom").defineInRange("Revervox Sonic Boom Range", 16, 5, 100);
        MEGAPHONE_COOLDOWN = BUILDER.comment("Cooldown (in seconds) between Megaphone uses").defineInRange("Megaphone Cooldown" ,4, 0, 100);
        CROUCH_PREVENTS_MEGAPHONE_BOOM = BUILDER.comment("Whether crouching will stop your megaphone from doing a sonic boom").define("Crouch stops Megaphone Boom", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
