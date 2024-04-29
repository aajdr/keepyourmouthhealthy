package xyz.starchenpy.keepyourmouthhealthy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import xyz.starchenpy.keepyourmouthhealthy.common.Config;
import xyz.starchenpy.keepyourmouthhealthy.common.advancements.ModTriggers;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;
import xyz.starchenpy.keepyourmouthhealthy.common.event.EntityEatListener;
import xyz.starchenpy.keepyourmouthhealthy.common.event.ItemRenderListener;
import xyz.starchenpy.keepyourmouthhealthy.common.item.ModItems;
import xyz.starchenpy.keepyourmouthhealthy.common.item.ModTabs;

@Mod(KeepYourMouthHealthy.MOD_ID)
public class KeepYourMouthHealthy {
    public static final String MOD_ID = "keepyourmouthhealthy";

    public KeepYourMouthHealthy(IEventBus modEventBus) {
        ModItems.register(modEventBus);
        ModTabs.register(modEventBus);
        ModEffects.register(modEventBus);
        ModTriggers.register(modEventBus);

        NeoForge.EVENT_BUS.register(EntityEatListener.class);
        NeoForge.EVENT_BUS.register(ItemRenderListener.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
