package dev.omialien.revervoxmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevervoxModClientConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PRIVACY_MODE;

    static {
        BUILDER.push("Client Configs for Revervox Mod");

        PRIVACY_MODE = BUILDER.comment("If your audios get saved to disk on the servers you play on").define("Privacy Mode", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
