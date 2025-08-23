package dev.omialien.revervoxmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RevervoxModServerConfigs {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue REVERVOX_MAX_AUDIOS = BUILDER.defineInRange("revervoxMaxAudios", 12, 1, 100);
    // TODO grace periods não aparecem na config screen, talvez por serem floats
    public static final ModConfigSpec.DoubleValue REVERVOX_AFTER_SPEAK_GRACE_PERIOD = BUILDER.defineInRange("revervoxGracePeriod", 1.5d, 0d, 5d);
    public static final ModConfigSpec.DoubleValue REVERVOX_BAT_AFTER_SPAWN_GRACE_PERIOD = BUILDER.defineInRange("revervoxBatGracePeriod", 0.5d, 0d, 5d);
    public static final ModConfigSpec.IntValue REVERVOX_BAT_SPAWN_CHANCE = BUILDER.defineInRange("revervoxBatSpawnChance", 5, 2, 500);
    public static final ModConfigSpec.IntValue REVERVOX_SWORD_BONUS_DAMAGE = BUILDER.defineInRange("revervoxSwordBonus", 7, 1, 20);
    public static final ModConfigSpec.IntValue REVERVOX_BAT_TOOTH_DROP_CHANCE = BUILDER.defineInRange("batToothDropChance", 10, 1, 20);
    public static final ModConfigSpec.IntValue REVERVOX_SPAWN_CHANCE = BUILDER.defineInRange("revervoxMinimumDistance", 100, 0, 500);
    public static final ModConfigSpec.DoubleValue FAKE_BAT_EVENT_CHANCE = BUILDER.defineInRange("batEventChance", 1d, 0.1d, 20d);
    public static final ModConfigSpec.BooleanValue REVERVOX_BREAKS_BLOCKS = BUILDER.define("revervoxBreaksBlocks", true);
    public static final ModConfigSpec.BooleanValue REVERVOX_ABOVE_GROUND = BUILDER.define("revervoxAboveGround", false);
    public static final ModConfigSpec.BooleanValue REVERVOX_BREAKS_NONSOLID = BUILDER.define("revervoxBreakNonSolid", true);
    public static final ModConfigSpec SPEC = BUILDER.build();
}
