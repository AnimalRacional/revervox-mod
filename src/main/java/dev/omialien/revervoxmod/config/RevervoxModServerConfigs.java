package dev.omialien.revervoxmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevervoxModServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_MAX_AUDIOS_TO_PLAY;
    // TODO grace periods não aparecem na config screen, talvez por serem floats
    public static final ForgeConfigSpec.ConfigValue<Double> REVERVOX_AFTER_SPEAK_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Double> REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SWORD_BONUS_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_BAT_TOOTH_DROP_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> REVERVOX_SPAWN_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> FAKE_BAT_EVENT_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REVERVOX_BREAKS_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REVERVOX_BREAKS_NONSOLID;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REVERVOX_ABOVE_GROUND;

    static {
        BUILDER.push("Server Configs for Revervox Mod");

        REVERVOX_MAX_AUDIOS_TO_PLAY = BUILDER.comment("Maximum audios that Revervox will play before disappearing").define("Revervox Max Audios", 20);
        REVERVOX_AFTER_SPEAK_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox will wait after speaking before being able to get angry").define("Revervox After Speaking Grace Period", 1.5d);
        REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD = BUILDER.comment("Time in seconds that Revervox Bat will wait after spawn before being able to get angry").define("Revervox Bat After Spawning Grace Period", 0.5d);
        REVERVOX_SPAWN_CHANCE = BUILDER.comment("Minimum distance between every Revervox").define("Revervox Spawn Chance", 100);
        REVERVOX_BAT_SPAWN_CHANCE = BUILDER.comment("Chance of Revervox Bat spawning (1 in x)").define("Revervox Bat Spawn Chance", 5);
        REVERVOX_SWORD_BONUS_DAMAGE = BUILDER.comment("How much extra damage the revervox sword deals to revervox entities").define("Revervox Sword Bonus Damage", 7);
        REVERVOX_BAT_TOOTH_DROP_CHANCE = BUILDER.comment("The chance of a revervox bat dropping its tooth on attack (1 in x)").define("Revervox Bat Tooth Drop Chance", 10);
        FAKE_BAT_EVENT_CHANCE = BUILDER.comment("Chance of fake bat event occuring. Higher is less likely, lower is more likely").define("Fake Bat Event Chance", 1.0d);
        REVERVOX_BREAKS_BLOCKS = BUILDER.comment("Whether Revervox breaks blocks to get to you").define("Revervox breaks blocks", false);
        REVERVOX_BREAKS_NONSOLID = BUILDER.comment("Whether Revervox breaks non-solid blocks such as torches and natural blocks such as dirt and stone").define("Revervox breaks non-solid blocks", true);
        REVERVOX_ABOVE_GROUND = BUILDER.comment("Whether Revervox spawns above ground").define("Revervox above ground", false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
