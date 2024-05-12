package xyz.starchenpy.keepyourmouthhealthy.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ToothpasteParticleOption implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ToothpasteParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Nonnull
        @Override
        @ParametersAreNonnullByDefault
        public ToothpasteParticleOption fromCommand(ParticleType<ToothpasteParticleOption> type, StringReader p_123722_) throws CommandSyntaxException {
            p_123722_.expect(' ');
            ItemParser.ItemResult itemparser$itemresult = ItemParser.parseForItem(BuiltInRegistries.ITEM.asLookup(), p_123722_);
            ItemStack itemstack = new ItemInput(itemparser$itemresult.item(), itemparser$itemresult.nbt()).createItemStack(1, false);
            return new ToothpasteParticleOption(itemstack);
        }

        @Nonnull
        @Override
        @ParametersAreNonnullByDefault
        public ToothpasteParticleOption fromNetwork(ParticleType<ToothpasteParticleOption> type, FriendlyByteBuf p_123725_) {
            return new ToothpasteParticleOption(p_123725_.readItem());
        }
    };
    private final ItemStack itemStack;

    public static Codec<ToothpasteParticleOption> codec() {
        return ItemStack.CODEC.xmap(ToothpasteParticleOption::new, from -> from.itemStack);
    }

    public ToothpasteParticleOption(ItemStack pItemStack) {
        this.itemStack = pItemStack.copy();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeItem(this.itemStack);
    }

    @Nonnull
    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType())
                + " "
                + new ItemInput(this.itemStack.getItemHolder(), this.itemStack.getTag()).serialize();
    }

    @Nonnull
    @Override
    public ParticleType<ToothpasteParticleOption> getType() {
        return ModParticleType.TOOTHPASTE_PARTICLE.get();
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}
