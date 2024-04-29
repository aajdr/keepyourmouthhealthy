package xyz.starchenpy.keepyourmouthhealthy.common.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BrushingTrigger extends SimpleCriterionTrigger<BrushingTrigger.TriggerInstance> {
    @NotNull
    @Override
    public Codec<BrushingTrigger.TriggerInstance> codec() {
        return BrushingTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(stack));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BrushingTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(TriggerInstance::item)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(ItemStack stack) {
            return this.item.isEmpty() || this.item.get().matches(stack);
        }
    }
}
