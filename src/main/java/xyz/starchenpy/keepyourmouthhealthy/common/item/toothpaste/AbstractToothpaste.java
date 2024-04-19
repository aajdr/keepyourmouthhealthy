package xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

public abstract class AbstractToothpaste extends Item {
    protected int maxLevel;

    /**
     * 牙膏的抽象类
     * @param item 物品属性
     * @param maxLevel 能刷掉的蛀牙的最大等级
     */
    public AbstractToothpaste(Properties item, int maxLevel) {
        super(item);
        this.maxLevel = maxLevel;
    }

    /**
     * 降低一级蛀牙效果
     * @param entity 玩家实体
     */
    protected void cleanTooth(LivingEntity entity) {
        MobEffectInstance effectToothDecay = entity.getEffect(ModEffects.TOOTH_DECAY.get());

        if (effectToothDecay == null) {
            return;
        }

        // 除蛀
        if (effectToothDecay.getAmplifier() == 0) {
            entity.removeEffect(effectToothDecay.getEffect());
        } else {
            if (effectToothDecay.getAmplifier() < maxLevel) {
                entity.removeEffect(effectToothDecay.getEffect());
                MobEffectInstance newEffect = new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1, effectToothDecay.getAmplifier() - 1);
                entity.addEffect(newEffect);
            }
        }
    }

    public abstract void effect(LivingEntity entity);
}
