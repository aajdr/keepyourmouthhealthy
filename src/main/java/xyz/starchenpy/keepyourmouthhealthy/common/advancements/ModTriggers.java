package xyz.starchenpy.keepyourmouthhealthy.common.advancements;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

public class ModTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, MOD_ID);

    public static final Supplier<BrushingTrigger> AFTER_BRUSHING_TEETH = TRIGGERS.register("after_brushing_teeth", BrushingTrigger::new);

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }
}
