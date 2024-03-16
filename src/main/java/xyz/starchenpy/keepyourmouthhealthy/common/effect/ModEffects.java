package xyz.starchenpy.keepyourmouthhealthy.common.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

public class ModEffects {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);

    public static final Supplier<MobEffect> TOOTH_DECAY = MOB_EFFECTS.register("tooth_decay", () ->
            new ModEffect(MobEffectCategory.BENEFICIAL, 0x999999));

    public static final Supplier<MobEffect> ORAL_INJURY = MOB_EFFECTS.register("oral_injury", () ->
            new ModEffect(MobEffectCategory.BENEFICIAL, 0xCC0000));

    public static final Supplier<MobEffect> HEALTHY_ORAL = MOB_EFFECTS.register("healthy_oral", () ->
            new ModEffect(MobEffectCategory.BENEFICIAL, 0xFFFF33));

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
