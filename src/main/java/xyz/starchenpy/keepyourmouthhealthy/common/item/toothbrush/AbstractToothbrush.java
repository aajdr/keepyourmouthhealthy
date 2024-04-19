package xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.AbstractToothpaste;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

public class AbstractToothbrush extends Item {
    protected static final String NBT_NAME = "toothpaste";

    public AbstractToothbrush(Properties item) {
        super(item);
    }

    /**
     * 使用自定义动画应返回 CUSTOM
     * @param itemStack 物品
     * @return 动画类型
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.CUSTOM;
    }

    /**
     * 决定了刷牙所需的时间
     * @param itemStack 物品
     * @return 时间(tick)
     */
    @Override
    @ParametersAreNonnullByDefault
    public int getUseDuration(ItemStack itemStack) {
        return 160;
    }

    /**
     * 决定了刷牙后冷却时间
     * @return 冷却时间(tick)
     */
    public int getCooldown() {
        return 400;
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

        String toothpasteName = itemStack.getOrCreateTag().getString(NBT_NAME);
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(toothpasteName));
        if (item instanceof AbstractToothpaste) {
            player.startUsingItem(hand);
            return InteractionResultHolder.pass(itemStack);
        }

        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack toothbrush = player.getItemInHand(hand);
        ItemStack toothpaste = player.getItemInHand(otherHand);

        if (toothpaste.getItem() instanceof AbstractToothpaste) {
            toothpaste.setDamageValue(toothpaste.getDamageValue() + 1);
            toothbrush.getOrCreateTag().putString(NBT_NAME, BuiltInRegistries.ITEM.getKey(toothpaste.getItem()).toString());
            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.fail(itemStack);
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
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(toothpasteName));
            if (item instanceof AbstractToothpaste toothpaste) {
                toothpaste.effect(player);
                itemStack.getTag().putString(NBT_NAME, "");
                player.getCooldowns().addCooldown(this, getCooldown());
            }
        }
        return itemStack;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                this.applyItemArmTransform(poseStack, arm, equipProcess);

                if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
                    if (itemInHand.getOrCreateTag().contains(NBT_NAME)) {
                        this.brushingArmTransform(poseStack, arm, player.getUseItemRemainingTicks(), itemInHand.getUseDuration(), partialTick);
                    } else {
                        this.applyToothpasteArmTransform(poseStack, arm);
                    }
                }

                return true;
            }

            /**
             * 物品取出时的上抬动作
             * @param poseStack 物品
             * @param hand 手
             * @param equipProcess 剑条进度
             */
            private void applyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equipProcess) {
                int i = hand == HumanoidArm.RIGHT ? 1 : -1;
                poseStack.translate((float)i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
            }

            private void applyToothpasteArmTransform(PoseStack poseStack, HumanoidArm hand) {

            }

            /**
             * 应用刷牙动作
             * @param poseStack 物品
             * @param arm 手
             * @param remainingDuration 玩家剩余的使用时间
             * @param useDuration 物品的使用时间
             */
            private void brushingArmTransform(PoseStack poseStack, HumanoidArm arm, int remainingDuration, int useDuration, float partialTick) {
                int i = arm == HumanoidArm.RIGHT ? 1 : -1;
                // 使用进度
                float progress = ((1 - ((float) remainingDuration / useDuration)) * 100) + partialTick;
                // 摆动幅度
                double amplitude = Math.sin(progress) / 8 * i;

                float angleZ = progress <= 10 ? i * progress * 9 : i * 90;
                float angleX = progress <= 10 ? progress * 8 : 80;
                poseStack.mulPose(Axis.ZP.rotationDegrees(angleZ));
                poseStack.mulPose(Axis.XP.rotationDegrees(angleX));

                // 根据进度调整位置
                if (progress <= 10) {
                    poseStack.translate(0, 0, progress / 100);
                } else if (progress <= 40) {
                    poseStack.translate(0, 0, amplitude);
                } else if (progress <= 70) {
                    poseStack.translate(amplitude / 2, 0, 0);
                } else {
                    poseStack.translate(0, 0, amplitude);
                }
            }
        });
    }
}
