package xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class AbstractToothbrush extends Item {
    protected static final String NBT = "toothpaste";

    public AbstractToothbrush(Properties item) {
        super(item);
    }

    /**
     * 使用时的动画 这里用的吃东西的动画，我觉得还蛮合适的
     * @param itemStack 物品
     * @return 动画类型
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.EAT;
    }

    /**
     * 决定了刷牙所需的时间
     * @param itemStack 物品
     * @return 时间(tick)
     */
    @Override
    @ParametersAreNonnullByDefault
    public int getUseDuration(ItemStack itemStack) {
        return 100;
    }

    /**
     * 清洁口腔
     * @param entity 实体
     * @param maximumCapacity 允许的最大蛀牙等级
     */
    protected void clean(Player entity, int maximumCapacity) {
        MobEffectInstance effectToothDecay = entity.getEffect(ModEffects.TOOTH_DECAY.get());

        //这边是除蛀
        if (effectToothDecay != null) {
            if (effectToothDecay.getAmplifier() == 0) {
                entity.removeEffect(effectToothDecay.getEffect());
            } else {
                if (effectToothDecay.getAmplifier() < maximumCapacity) {
                    entity.removeEffect(effectToothDecay.getEffect());
                    MobEffectInstance newEffect = new MobEffectInstance(ModEffects.TOOTH_DECAY.get(), -1, effectToothDecay.getAmplifier() - 1);
                    entity.addEffect(newEffect);
                }
            }
        } else {
            //这边是加口腔清洁buff
            entity.addEffect(new MobEffectInstance(ModEffects.HEALTHY_ORAL.get(), 9600));
        }
    }
}
