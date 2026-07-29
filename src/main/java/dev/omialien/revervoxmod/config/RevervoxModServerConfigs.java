package dev.omialien.revervoxmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevervoxModServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> RECORDING_LIMIT;
    public static final ForgeConfigSpec.ConfigValue<Integer> MINIMUM_AUDIO_COUNT;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_MAX_AUDIOS;
    public static final ForgeConfigSpec.IntValue REVERVOX_UNHEARD_BEFORE_DISAPPEAR;
    public static final ForgeConfigSpec.ConfigValue<Double> REVERVOX_AFTER_SPEAK_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Double> REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SWORD_BONUS_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_TOOTH_DROP_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_MIN_DISTANCE;
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
    public static final ForgeConfigSpec.IntValue REVERVOX_SPAWN_COOLDOWN;
    public static final ForgeConfigSpec.DoubleValue REVERVOX_COOLDOWN_RANGE;
    public static final ForgeConfigSpec.IntValue REVERVOX_GIVE_UP_SILENT;
    public static final ForgeConfigSpec.IntValue REVERVOX_FORGET_PAIN;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FAKE_REVERVOX_BEHIND_EVENT;
    public static final ForgeConfigSpec.ConfigValue<Double> FAKE_REVERVOX_BEHIND_EVENT_CHANCE;

    static {
        BUILDER.push("Server Configs for Revervox Mod");

        RECORDING_LIMIT = BUILDER.comment("The maximum amount of player recordings that can be saved").define("Maximum Saved Recordings", 200);
        MINIMUM_AUDIO_COUNT = BUILDER.comment("The minimum amount of audios before they start being deleted").define("Minimum Audio Count", 50);

        REVERVOX_MAX_AUDIOS = BUILDER.comment("Maximum audios that Revervox will play before disappearing").define("Revervox Max Audios", 12);
        REVERVOX_UNHEARD_BEFORE_DISAPPEAR = BUILDER.comment("Amount of times Revervox will try to speak without anyone nearby before disappearing").defineInRange("Revervox Unheard Before Disappearing", 10, 1, 100);
        REVERVOX_AFTER_SPEAK_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox will wait after speaking before being able to get angry").define("Revervox After Speaking Grace Period", 1.5d);
        REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox Bat will wait after spawn before being able to get angry").define("Revervox Bat After Spawning Grace Period", 0.5d);
        REVERVOX_SPAWN_CHANCE = BUILDER.comment("Chance of Revervox spawning (1 in x)").defineInRange("Revervox Spawn Chance", 2, 1, Integer.MAX_VALUE);
        REVERVOX_MIN_DISTANCE = BUILDER.comment("Minimum distance between every Revervox").define("Revervox Minimum Distance", 100);
        REVERVOX_BAT_SPAWN_CHANCE = BUILDER.comment("Chance of Revervox Bat spawning (1 in x)").define("Revervox Bat Spawn Chance", 5);
        REVERVOX_SWORD_BONUS_DAMAGE = BUILDER.comment("How much extra damage the Revervox sword deals to Revervox entities").define("Revervox Sword Bonus Damage", 7);
        REVERVOX_BAT_TOOTH_DROP_CHANCE = BUILDER.comment("The chance of a Revervox bat dropping its tooth on attack (1 in x)").define("Revervox Bat Tooth Drop Chance", 10);
        FAKE_BAT_EVENT_CHANCE = BUILDER.comment("Chance of fake bat event occurring. Higher is less likely, lower is more likely").define("Fake Bat Event Chance", 1.0d);
        FAKE_REVERVOX_BEHIND_EVENT_CHANCE = BUILDER.comment("Chance of fake Revervox behind event occurring. Higher is less likely, lower is more likely").define("Fake Revervox Behind Event Chance", 1.0d);
        ENABLE_FAKE_BAT_EVENT = BUILDER.comment("Whether the fake bat event will occur").define("Enable Fake Bat Event", true);
        ENABLE_FAKE_REVERVOX_BEHIND_EVENT = BUILDER.comment("Whether the Fake Revervox behind player event will occur (not seeing other players during it is intended)").define("Enable Fake Revervox Behind Event", true);
        REVERVOX_BREAKS_BLOCKS = BUILDER.comment("Whether Revervox breaks blocks to get to you").define("Revervox Block Breaking", false);
        REVERVOX_BREAKS_NONSOLID = BUILDER.comment("Whether Revervox breaks non-solid blocks such as torches and leaves").define("Revervox breaks non-solid blocks", true);
        REVERVOX_ABOVE_GROUND = BUILDER.comment("Whether Revervox spawns above ground").define("Revervox above ground", false);
        REVERVOX_SPAWN_COOLDOWN = BUILDER.comment("How long, in ticks, Revervox will avoid spawning near a player after spawning close to them").defineInRange("Revervox Spawn Cooldown", 6000, 0, Integer.MAX_VALUE);
        REVERVOX_COOLDOWN_RANGE = BUILDER.comment("The range at which players will be put into Revervox spawning cooldown after it spawns or attacks. If above 10000, the cooldown will be global").defineInRange("Revervox Cooldown Range", 200.0, 1.0, Double.MAX_VALUE);

        REVERVOX_SONIC_BOOM = BUILDER.comment("Whether Revervox uses the Sonic Boom ability").define("Revervox Sonic Boom", true);
        REVERVOX_SONIC_BOOM_COOLDOWN = BUILDER.comment("Cooldown (in seconds) between Revervox Sonic Booms").defineInRange("Revervox Sonic Boom Cooldown", 35, 1, 100);
        REVERVOX_SONIC_BOOM_SECS_OUT_OF_SIGHT = BUILDER.comment("Time (in seconds) that Revervox needs to be out of sight before being able to use a sonic boom").defineInRange("Revervox Sonic Boom Secs out of Sight", 5, 1, 100);
        REVERVOX_SONIC_BOOM_RANGE = BUILDER.comment("Range (in blocks) of Revervox's Sonic Boom").defineInRange("Revervox Sonic Boom Range", 16, 5, 100);
        MEGAPHONE_COOLDOWN = BUILDER.comment("Cooldown (in seconds) between Megaphone uses").defineInRange("Megaphone Cooldown" ,4, 0, 100);
        CROUCH_PREVENTS_MEGAPHONE_BOOM = BUILDER.comment("Whether crouching will stop your megaphone from doing a sonic boom").define("Crouch stops Megaphone Boom", true);
        REVERVOX_GIVE_UP_SILENT = BUILDER.comment("How long (in milliseconds) you have to stop speaking for Revervox to lose track of you").defineInRange("Revervox Silence Give Up Time", 10000, 1, Integer.MAX_VALUE);
        REVERVOX_FORGET_PAIN = BUILDER.comment("How many ticks it will take for Revervox to lose track of the person who last attacked it").defineInRange("Revervox Forget Pain Timer", 300, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
