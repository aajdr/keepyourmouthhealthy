package xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

public abstract class AbstractToothpaste extends Item {
    public AbstractToothpaste(Properties item) {
        super(item);
    }

    protected void cleanTooth(LivingEntity entity, int level) {
        MobEffectInstance effectToothDecay = entity.getEffect(ModEffects.TOOTH_DECAY.get());

        if (effectToothDecay == null) {
            return;
        }

        // 除蛀
        if (effectToothDecay.getAmplifier() == 0) {
            entity.removeEffect(effectToothDecay.getEffect());
        } else {
            if (effectToothDecay.getAmplifier() < level) {
                entity.removeEffect(effectToothDecay.getEffect());
                MobEffectInstance newEffect = new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1, effectToothDecay.getAmplifier() - 1);
                entity.addEffect(newEffect);
            }
        }
    }

    public abstract void effect(LivingEntity entity);
}
