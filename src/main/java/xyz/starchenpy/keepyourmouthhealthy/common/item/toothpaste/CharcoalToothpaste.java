package xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;

public class CharcoalToothpaste extends AbstractToothpaste {
    public CharcoalToothpaste(Properties item) {
        super(item);
    }

    @Override
    public void effect(LivingEntity entity) {
        cleanTooth(entity, 2);

        // 加口腔清洁buff
        entity.addEffect(new MobEffectInstance(ModEffects.HEALTHY_ORAL.get(), 9600));
    }
}
