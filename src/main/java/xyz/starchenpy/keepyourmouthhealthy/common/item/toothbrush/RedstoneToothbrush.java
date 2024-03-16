package xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import xyz.starchenpy.keepyourmouthhealthy.common.item.ModItems;
import xyz.starchenpy.keepyourmouthhealthy.common.item.Toothpaste;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

public class RedstoneToothbrush extends AbstractToothbrush {
    protected static final String NBT2 = "powerful";

    public RedstoneToothbrush(Properties item) {
        super(item);
    }

    /**
     * 决定了刷牙所需的时间
     * @param itemStack 物品
     * @return 时间(tick)
     */
    @Override
    @ParametersAreNonnullByDefault
    public int getUseDuration(ItemStack itemStack) {
        return 60;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!itemStack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(NBT, false);
            tag.putBoolean(NBT2, false);
            itemStack.setTag(tag);
        }

        assert itemStack.getTag() != null;
        if (!itemStack.getTag().contains(NBT)) {
            itemStack.getTag().putBoolean(NBT, false);
            itemStack.getTag().putBoolean(NBT2, false);
        }

        assert itemStack.getTag() != null;
        if (itemStack.getTag().getBoolean(NBT)) {
            player.startUsingItem(hand);
        } else {
            ItemStack offhandItem = player.getOffhandItem();
            if (offhandItem.getItem() instanceof Toothpaste) {
                if (offhandItem.getDamageValue() <= offhandItem.getMaxDamage()) {
                    offhandItem.setDamageValue(offhandItem.getDamageValue() + 1);
                    itemStack.getTag().putBoolean(NBT, true);
                    if (offhandItem.getItem().equals(ModItems.POWERFUL_ABRASIVE_PASTE.get())) {
                        itemStack.getTag().putBoolean(NBT2, true);
                    }
                } else {
                    return InteractionResultHolder.fail(itemStack);
                }
            } else {
                return InteractionResultHolder.pass(itemStack);
            }
        }

        return InteractionResultHolder.success(itemStack);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            //完成刷牙
            player.getCooldowns().addCooldown(this, 200);

            assert itemStack.getTag() != null;
            if (itemStack.getTag().getBoolean(NBT)) {
                clean(player, 4);

                if (itemStack.getTag().getBoolean(NBT2)) {
                    entity.hurt(entity.damageSources().magic(), 4.0f);
                }
            }

            itemStack.getTag().putBoolean(NBT, false);
            itemStack.getTag().putBoolean(NBT2, false);
        }
        return itemStack;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip." + MOD_ID + ".redstone_toothbrush"));
    }
}
