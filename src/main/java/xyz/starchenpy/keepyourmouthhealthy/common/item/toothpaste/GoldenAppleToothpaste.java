package xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

public class GoldenAppleToothpaste extends AbstractToothpaste {
    public GoldenAppleToothpaste(Properties item) {
        super(item, 4);
    }

    @Override
    public void effect(LivingEntity entity) {
        cleanTooth(entity);
        entity.removeEffect(ModEffects.INJURY_ORAL.get());
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3600, 0));
        entity.addEffect(new MobEffectInstance(ModEffects.CLEAN_ORAL.get(), 9600));
    }
}
