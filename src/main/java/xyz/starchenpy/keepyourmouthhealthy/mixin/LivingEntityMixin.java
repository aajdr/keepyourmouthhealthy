package xyz.starchenpy.keepyourmouthhealthy.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.starchenpy.keepyourmouthhealthy.common.Config;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract MobEffectInstance getEffect(MobEffect pEffect);

    @Redirect(method = "shouldTriggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I"))
    public int getUseDuration(ItemStack instance) {
        int duration = instance.getUseDuration();

        MobEffectInstance effect = getEffect(ModEffects.TOOTH_DECAY.get());

        if (effect != null) {
            int extraDuration = (int) ((effect.getAmplifier() + 1) * Config.extraEatTime * 0.85);
            return duration + extraDuration;
        }

        return duration;
    }
}
