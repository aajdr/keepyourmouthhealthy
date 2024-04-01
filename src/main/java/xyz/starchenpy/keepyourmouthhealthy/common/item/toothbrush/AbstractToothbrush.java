package xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.AbstractToothpaste;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class AbstractToothbrush extends Item {
    protected static final String NBT_NAME = "toothpaste";

    public AbstractToothbrush(Properties item) {
        super(item);
    }

    /**
     * 使用时的动画
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
     * 决定了刷牙后冷却时间
     * @return 冷却时间(tick)
     */
    public int getCooldown() {
        return 200;
    }

    /**
     * 使用牙刷时
     * @param level 世界
     * @param player 使用物品的玩家
     * @param hand 使用手
     * @return 使用结果
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!itemStack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putString(NBT_NAME, "");
            itemStack.setTag(tag);
        }

        // 这里已知上面添加了Tag，所以这里直接断言来消除警告
        assert itemStack.getTag() != null;
        String toothpasteName = itemStack.getTag().getString(NBT_NAME);
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(toothpasteName));
        if (item instanceof AbstractToothpaste) {
            player.startUsingItem(hand);
            return InteractionResultHolder.success(itemStack);
        } else {
            return applyToothpaste(player, hand) ? InteractionResultHolder.success(itemStack) : InteractionResultHolder.fail(itemStack);
        }
    }

    /**
     * 抹牙膏
     * @param player 使用物品的玩家
     * @param hand 使用手
     */
    protected boolean applyToothpaste(Player player, InteractionHand hand) {
        // 推断另一只手
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;

        ItemStack toothbrush = player.getItemInHand(hand);
        ItemStack toothpaste = player.getItemInHand(otherHand);

        assert toothbrush.getTag() != null;
        if (toothpaste.getItem() instanceof AbstractToothpaste) {
            toothpaste.setDamageValue(toothpaste.getDamageValue() + 1);
            toothbrush.getTag().putString(NBT_NAME, BuiltInRegistries.ITEM.getKey(toothpaste.getItem()).toString());
            return true;
        }

        return false;
    }

    /**
     * 使用完成后的逻辑
     * @param itemStack 物品
     * @param level 世界
     * @param entity 使用者
     * @return 物品
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (itemStack.getTag() == null) {
                return itemStack;
            }

            String toothpasteName = itemStack.getTag().getString(NBT_NAME);
            if (toothpasteName.isEmpty()) {
                return itemStack;
            }

            // 根据 NBT 中的名字获取 Item
            System.out.println("这是获取到的名字" + toothpasteName);
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(toothpasteName));
            if (item instanceof AbstractToothpaste toothpaste) {
                toothpaste.effect(player);
                itemStack.getTag().putString(NBT_NAME, "");
                player.getCooldowns().addCooldown(this, getCooldown());
            }
        }
        return itemStack;
    }
}
