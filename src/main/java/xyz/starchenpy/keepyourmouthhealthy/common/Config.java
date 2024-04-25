package xyz.starchenpy.keepyourmouthhealthy.common;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy;

@Mod.EventBusSubscriber(modid = KeepYourMouthHealthy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue CHANCE_OF_TOOTH_DECAY = BUILDER
            .comment("This value determines the chance of tooth decay while eating")
            .defineInRange("chanceOfToothDecay", 5, 0, 100);

    private static final ModConfigSpec.IntValue TOOTH_DECAY_MAX_LEVEL = BUILDER
            .comment("This value determines the maximum level of tooth decay buff that can be obtained normally")
            .defineInRange("toothDecayMaxLevel", 4, 0, 255);

    private static final ModConfigSpec.IntValue ORAL_INJURY_MAX_LEVEL = BUILDER
            .comment("This value determines the maximum level of oral injury buff that can be obtained normally")
            .defineInRange("toothDecayMaxLevel", 3, 0, 255);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int chanceOfToothDecay;
    public static int toothDecayMaxLevel;
    public static int oralInjuryMaxLevel;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        chanceOfToothDecay = CHANCE_OF_TOOTH_DECAY.get();
        toothDecayMaxLevel = TOOTH_DECAY_MAX_LEVEL.get();
        oralInjuryMaxLevel = ORAL_INJURY_MAX_LEVEL.get();
    }
}
