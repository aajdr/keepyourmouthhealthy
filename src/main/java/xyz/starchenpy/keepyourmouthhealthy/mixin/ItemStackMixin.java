package xyz.starchenpy.keepyourmouthhealthy.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAccessor {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    public void getUseDuration(CallbackInfoReturnable<Integer> cir) {
        int result;

        result = getItem().getUseDuration(((ItemStack)(Object)this));

        cir.setReturnValue(result);
    }
}
