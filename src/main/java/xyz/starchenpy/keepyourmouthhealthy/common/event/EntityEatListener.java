package xyz.starchenpy.keepyourmouthhealthy.common.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import xyz.starchenpy.keepyourmouthhealthy.common.Config;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;
import xyz.starchenpy.keepyourmouthhealthy.mixin.ItemStackMixin;

public class EntityEatListener {
    @SubscribeEvent
    public static void entityEatListener(LivingEntityUseItemEvent.Finish event) {
        if (!event.getItem().isEdible()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            givePlayerEffect(player);
            eatFinish(player, event.getItem());
        }
    }

    @SubscribeEvent
    public static void entityEatListener(LivingEntityUseItemEvent.Start event) {
        if (!event.getItem().isEdible()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = player.getEffect(ModEffects.TOOTH_DECAY.get());
            if (effect != null) {
                int useDuration = event.getDuration();
                int extraDuration = effect.getAmplifier() * 16 + 16;
                // 增加吃饭的时间
                event.setDuration(useDuration + extraDuration);
            }
        }
    }

    /**
     * 该方法用于给吃完东西的玩家上 buff
     * @param player 玩家
     */
    private static void givePlayerEffect(Player player) {
        MobEffectInstance buffToothDecay = player.getEffect(ModEffects.TOOTH_DECAY.get());
        MobEffectInstance buffOralInjury = player.getEffect(ModEffects.ORAL_INJURY.get());
        MobEffectInstance bufferHealthyOral = player.getEffect(ModEffects.HEALTHY_ORAL.get());

        //如果有口腔健康 buff 就不给新 buff
        if (bufferHealthyOral != null) {
            return;
        }

        // 如果没有蛀牙 buff 就给一个
        if (buffToothDecay == null) {
            player.addEffect(new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1));
            return;
        }

        //等级限制
        if (buffToothDecay.getAmplifier() < Config.toothDecayMaxLevel) {
            buffToothDecay.update(new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1, buffToothDecay.getAmplifier() + 1));
        } else if (buffOralInjury != null) {
            //等级限制
            if (buffToothDecay.getAmplifier() < Config.oralInjuryMaxLevel) {
                buffToothDecay.update(new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1, buffOralInjury.getAmplifier() + 1));
            }
        } else {
            player.addEffect(new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1));
        }
    }

    private static void eatFinish(Player player, ItemStack item) {
        MobEffectInstance buffOralInjury = player.getEffect(ModEffects.ORAL_INJURY.get());
        MobEffectInstance bufferHealthyOral = player.getEffect(ModEffects.HEALTHY_ORAL.get());

        if (buffOralInjury != null) {
            int level = buffOralInjury.getAmplifier();
            player.hurt(player.damageSources().magic(), 2 + level * 2);
        }

        if (bufferHealthyOral != null) {
            FoodProperties foodProperties = item.getFoodProperties(player);
            if (foodProperties != null) {
                int nutrition = (int) (foodProperties.getNutrition() * 0.2);
                float saturation = (float) (foodProperties.getSaturationModifier() * 0.2);
                player.getFoodData().eat(nutrition, saturation);
            }
        }
    }
}
