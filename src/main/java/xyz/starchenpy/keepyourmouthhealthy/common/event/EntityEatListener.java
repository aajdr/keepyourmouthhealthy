package xyz.starchenpy.keepyourmouthhealthy.common.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import xyz.starchenpy.keepyourmouthhealthy.common.Config;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;
import xyz.starchenpy.keepyourmouthhealthy.common.util.EffectUtil;

import java.util.Random;

public class EntityEatListener {
    @SubscribeEvent
    public static void entityEatFinishListener(LivingEntityUseItemEvent.Finish event) {
        if (!event.getItem().isEdible()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            givePlayerEffect(player);
            eatFinish(player, event.getItem());
        }
    }

    @SubscribeEvent
    public static void entityEatStartListener(LivingEntityUseItemEvent.Start event) {
        if (!event.getItem().isEdible()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = player.getEffect(ModEffects.TOOTH_DECAY.get());
            if (effect != null) {
                int useDuration = event.getDuration();
                int extraDuration = (effect.getAmplifier() + 1) * Config.extraEatTime;
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

        //如果有口腔健康 buff 就不会蛀牙
        if (effectHealthyOral != null) {
            return;
        }

        if (new Random().nextInt(100) > Config.chanceOfToothDecay) {
            return;
        }

        // 给予蛀牙 buff
        if (effectToothDecay == null) {
            player.addEffect(new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1));
            return;
        }
        int toothDecayAmplifier = effectToothDecay.getAmplifier();
        if (toothDecayAmplifier < Config.toothDecayMaxLevel) {
            EffectUtil.updateEffect(player, effectToothDecay, toothDecayAmplifier + 1);
            return;
        }

        // 给予口腔损伤 buff
        if (effectOralInjury == null) {
            player.addEffect(new MobEffectInstance(ModEffects.ORAL_INJURY.get(), 2400));
            return;
        }
        int oralInjuryAmplifier = effectOralInjury.getAmplifier();
        int duration = 2400 * (oralInjuryAmplifier + 1);
        if (oralInjuryAmplifier < Config.oralInjuryMaxLevel) {
            EffectUtil.updateEffect(player, effectOralInjury, duration, oralInjuryAmplifier + 1);
        } else {
            EffectUtil.updateEffect(player, effectOralInjury, duration, oralInjuryAmplifier);
        }
    }

    private static void eatFinish(Player player, ItemStack item) {
        MobEffectInstance effectOralInjury = player.getEffect(ModEffects.ORAL_INJURY.get());
        MobEffectInstance effectHealthyOral = player.getEffect(ModEffects.HEALTHY_ORAL.get());

        if (effectOralInjury != null) {
            int damage = (effectOralInjury.getAmplifier() + 1) * Config.eatingDamage;
            player.hurt(player.damageSources().magic(), damage);
        }

        if (effectHealthyOral != null && item.isEdible()) {
            FoodProperties foodProperties = item.getFoodProperties(player);
            // item.isEdible() 已经判断过不为 null 了，这里加断言消除警告
            assert foodProperties != null;
            int nutrition = (int) (foodProperties.getNutrition() * Config.extraNutrition);
            float saturation = foodProperties.getSaturationModifier() * Config.extraSaturation;
            player.getFoodData().eat(nutrition, saturation);
        }
    }
}
