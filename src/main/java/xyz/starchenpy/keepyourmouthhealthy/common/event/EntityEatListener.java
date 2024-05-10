package xyz.starchenpy.keepyourmouthhealthy.common.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import xyz.starchenpy.keepyourmouthhealthy.common.Config;
import xyz.starchenpy.keepyourmouthhealthy.common.ModDamageType;
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
            eatFinish(player, event.getItem());
            givePlayerEffect(player);
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

    private static void givePlayerEffect(Player player) {
        MobEffectInstance effectToothDecay = player.getEffect(ModEffects.TOOTH_DECAY.get());
        MobEffectInstance effectInjuryOral = player.getEffect(ModEffects.INJURY_ORAL.get());
        MobEffectInstance effectCleanOral = player.getEffect(ModEffects.CLEAN_ORAL.get());

        //如果有口腔洁净 buff 就不会蛀牙
        if (effectCleanOral != null) {
            return;
        }

        if (new Random().nextInt(100) > Config.chanceOfToothDecay) {
            return;
        }

        if (effectToothDecay == null) {
            MobEffectInstance instance = new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1);
            // 设定无法被牛奶移除
            instance.getCures().remove(EffectCures.MILK);
            player.addEffect(instance);
            return;
        }
        int toothDecayAmplifier = effectToothDecay.getAmplifier();
        if (toothDecayAmplifier < Config.toothDecayMaxLevel) {
            EffectUtil.updateEffect(player, effectToothDecay, toothDecayAmplifier + 1);
            return;
        }

        if (effectInjuryOral == null) {
            player.addEffect(new MobEffectInstance(ModEffects.INJURY_ORAL.get(), 2400));
            return;
        }
        int oralInjuryAmplifier = effectInjuryOral.getAmplifier();
        int duration = 2400 * (oralInjuryAmplifier + 1);
        if (oralInjuryAmplifier < Config.oralInjuryMaxLevel) {
            EffectUtil.updateEffect(player, effectInjuryOral, duration, oralInjuryAmplifier + 1);
        } else {
            EffectUtil.updateEffect(player, effectInjuryOral, duration, oralInjuryAmplifier);
        }
    }

    private static void eatFinish(Player player, ItemStack item) {
        MobEffectInstance effectInjuryOral = player.getEffect(ModEffects.INJURY_ORAL.get());
        MobEffectInstance effectCleanOral = player.getEffect(ModEffects.CLEAN_ORAL.get());

        if (effectInjuryOral != null) {
            int damage = (effectInjuryOral.getAmplifier() + 1) * Config.eatingDamage;
            player.hurt(player.damageSources().source(ModDamageType.ORAL_BLEEDING), damage);
        }

        if (effectCleanOral != null && item.isEdible()) {
            FoodProperties foodProperties = item.getFoodProperties(player);
            // 已经判断过不为 null 了，这里加断言消除警告
            assert foodProperties != null;
            int nutrition = (int) (foodProperties.getNutrition() * Config.extraNutrition);
            float saturation = foodProperties.getSaturationModifier() * Config.extraSaturation;
            player.getFoodData().eat(nutrition, saturation);
        }
    }
}
