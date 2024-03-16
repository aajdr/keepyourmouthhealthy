package xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xyz.starchenpy.keepyourmouthhealthy.common.item.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class Toothbrush extends AbstractToothbrush {

    public Toothbrush(Properties item) {
        super(item);
    }

    /**
     * 右键时的操作
     * @param level 没啥用
     * @param player 玩家实体
     * @param hand 手
     * @return 结果
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        //如果没有NBT就给他加上
        if (!itemStack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(NBT, false);
            itemStack.setTag(tag);
        }

        //如果NBT中没有toothpaste就给他加上
        assert itemStack.getTag() != null;
        if (!itemStack.getTag().contains(NBT)) {
            itemStack.getTag().putBoolean(NBT, false);
        }

        //判断NBT中toothpaste的值
        assert itemStack.getTag() != null;
        if (itemStack.getTag().getBoolean(NBT)) {
            //如果有牙膏就刷牙
            player.startUsingItem(hand);
        } else {
            //如果牙刷上没有牙膏，判断副手上有没有牙膏
            ItemStack offhandItem = player.getOffhandItem();
            if (offhandItem.getItem().equals(ModItems.TOOTHPASTE.get())) {
                //如果有牙膏且牙膏的耐久度大于0就给牙刷抹上牙膏，牙膏耐久度减1
                if (offhandItem.getDamageValue() <= offhandItem.getMaxDamage()) {
                    offhandItem.setDamageValue(offhandItem.getDamageValue() + 1);
                    itemStack.getTag().putBoolean(NBT, true);
                } else {
                    //否则就失败
                    return InteractionResultHolder.fail(itemStack);
                }
            } else {
                //没有牙膏就跳过
                return InteractionResultHolder.pass(itemStack);
            }
        }

        return InteractionResultHolder.success(itemStack);
    }

    /**
     * 使用时间结束后的事件
     * @param itemStack 物品
     * @param level 没啥用
     * @param entity 使用物品的实体
     * @return 物品本身
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            //完成刷牙
            player.getCooldowns().addCooldown(this, 200);

            assert itemStack.getTag() != null;
            if (itemStack.getTag().getBoolean(NBT)) {
                clean(player, 2);
            }

            itemStack.getTag().putBoolean(NBT, false);
        }
        return itemStack;
    }
}
