package xyz.starchenpy.keepyourmouthhealthy.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.starchenpy.keepyourmouthhealthy.common.Config;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Redirect(method = "applyEatTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I"))
    public int getUseDuration(ItemStack instance) {
        int duration = instance.getUseDuration();

        MobEffectInstance effect = null;
        if (this.minecraft.player != null) {
            effect = this.minecraft.player.getEffect(ModEffects.TOOTH_DECAY.get());
        }

        if (effect != null) {
            int extraDuration = (effect.getAmplifier() + 1) * Config.extraEatTime;
            return duration + extraDuration;
        }

        return duration;
    }
}
