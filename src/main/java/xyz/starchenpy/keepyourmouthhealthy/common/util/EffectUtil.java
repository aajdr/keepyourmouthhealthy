package xyz.starchenpy.keepyourmouthhealthy.common.util;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

public class EffectUtil {
    public static void updateEffect(LivingEntity entity, MobEffectInstance effect, int amplifier) {
        updateEffect(entity, effect, 0, amplifier);
    }

    /**
     * 用于更新实体药水效果，上升下降皆可
     */
    public static void updateEffect(LivingEntity entity, MobEffectInstance effect, int duration, int amplifier) {
        duration = duration != 0 ? duration : effect.getDuration();
        int oldLevel = effect.getAmplifier();

        if (oldLevel > amplifier) {
            entity.removeEffect(effect.getEffect());

            if (amplifier >= 0) {
                MobEffectInstance newEffect = new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), duration, amplifier);
                entity.addEffect(newEffect);
            }
        } else {
            effect.update(new MobEffectInstance(effect.getEffect(), duration, amplifier));
            // effect.update() 不会触发成就，这里自行触发一次
            if (entity instanceof ServerPlayer player) {
                CriteriaTriggers.EFFECTS_CHANGED.trigger(player, player);
            }
        }
    }
}
