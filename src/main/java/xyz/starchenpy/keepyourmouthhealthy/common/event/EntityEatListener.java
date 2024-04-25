package xyz.starchenpy.keepyourmouthhealthy.common.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import xyz.starchenpy.keepyourmouthhealthy.common.Config;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

import java.util.Random;

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
                // 增加吃东西的时间
                event.setDuration(useDuration + extraDuration);
            }
        }
    }

    /**
     * 该方法用于给吃完东西的玩家上 buff
     * @param player 玩家
     */
    private static void givePlayerEffect(Player player) {
        MobEffectInstance effectToothDecay = player.getEffect(ModEffects.TOOTH_DECAY.get());
        MobEffectInstance effectOralInjury = player.getEffect(ModEffects.ORAL_INJURY.get());
        MobEffectInstance effectHealthyOral = player.getEffect(ModEffects.HEALTHY_ORAL.get());

        //如果有口腔健康 buff 就不给新 buff
        if (effectHealthyOral != null) {
            return;
        }

        if (new Random().nextInt() >= Config.chanceOfToothDecay) {
            return;
        }

        // 如果没有蛀牙 buff 就给一个
        if (effectToothDecay == null) {
            player.addEffect(new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1));
            return;
        }

        //等级限制
        if (effectToothDecay.getAmplifier() < Config.toothDecayMaxLevel) {
            effectToothDecay.update(new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1, effectToothDecay.getAmplifier() + 1));
        } else if (effectOralInjury != null) {
            if (effectOralInjury.getAmplifier() < Config.oralInjuryMaxLevel) {
                int duration = 2400 * (effectOralInjury.getAmplifier() + 2);
                effectOralInjury.update(new MobEffectInstance(ModEffects.ORAL_INJURY.get(), duration, effectOralInjury.getAmplifier() + 1));
            }
        } else {
            player.addEffect(new MobEffectInstance(ModEffects.ORAL_INJURY.get(), 2400));
        }
    }

    private static void eatFinish(Player player, ItemStack item) {
        MobEffectInstance effectOralInjury = player.getEffect(ModEffects.ORAL_INJURY.get());
        MobEffectInstance effectHealthyOral = player.getEffect(ModEffects.HEALTHY_ORAL.get());

        if (effectOralInjury != null) {
            int level = effectOralInjury.getAmplifier();
            player.hurt(player.damageSources().magic(), 2 + level * 2);
        }

        if (effectHealthyOral != null) {
            FoodProperties foodProperties = item.getFoodProperties(player);
            if (foodProperties != null) {
                int nutrition = (int) (foodProperties.getNutrition() * 0.2);
                float saturation = (float) (foodProperties.getSaturationModifier() * 0.2);
                player.getFoodData().eat(nutrition, saturation);
            }
        }
    }
}
