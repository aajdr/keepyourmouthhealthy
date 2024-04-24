package xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.AbstractToothpaste;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

public class AbstractToothbrush extends Item {
    protected static final String NBT_NAME = "toothpaste";
    protected int useDuration;

    public AbstractToothbrush(Properties item) {
        this(item, 160);
    }

    public AbstractToothbrush(Properties item, int useDuration) {
        super(item);
        this.useDuration = useDuration;
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
        if (getToothpaste(itemStack) != null) {
            return useDuration;
        }

        return 40;
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
        InteractionHand offHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack toothbrush = player.getItemInHand(hand);
        ItemStack toothpaste = player.getItemInHand(offHand);

        // 刷牙或者抹牙膏
        if (toothpaste.getItem() instanceof AbstractToothpaste || getToothpaste(toothbrush) instanceof AbstractToothpaste) {
            player.startUsingItem(hand);
            return InteractionResultHolder.pass(toothbrush);
        }

        return InteractionResultHolder.fail(toothbrush);
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
            ItemStack toothpaste = player.getMainHandItem() == itemStack ? player.getOffhandItem() : player.getMainHandItem();

            if (getToothpaste(itemStack) instanceof AbstractToothpaste item) {
                item.effect(player);
                setToothpaste(itemStack, null);
                player.getCooldowns().addCooldown(this, getCooldown());
            } else if (toothpaste.getItem() instanceof AbstractToothpaste item) {
                toothpaste.hurtAndBreak(1, player, (e) -> {});
                setToothpaste(itemStack, item);
            }
        }
        return itemStack;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (!pLevel.isClientSide) {
            return;
        }

        float progress = ((1 - ((float) pRemainingUseDuration / this.getUseDuration(pStack))) * 100);

        if (progress < 10) {
            return;
        }

        if (getToothpaste(pStack) instanceof AbstractToothpaste) {
            spawnToothpasteParticles(pLevel, pLivingEntity, pStack);
        }
    }

    private void spawnToothpasteParticles(Level level, LivingEntity entity, ItemStack stack) {
        Vec3 speedVec3 = new Vec3(((double)level.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
        speedVec3 = speedVec3.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
        speedVec3 = speedVec3.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));

        double d0 = (double)(-level.random.nextFloat()) * 0.6 - 0.3;
        Vec3 posVec3 = new Vec3(((double)level.random.nextFloat() - 0.5) * 0.3, d0, 0.6);
        posVec3 = posVec3.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
        posVec3 = posVec3.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
        posVec3 = posVec3.add(entity.getX(), entity.getEyeY(), entity.getZ());

        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), posVec3.x, posVec3.y, posVec3.z, speedVec3.x, speedVec3.y + 0.05, speedVec3.z);
    }

    /**
     * 从 NBT 获取牙膏
     * @param itemStack 物品
     * @return 牙膏
     */
    public static Item getToothpaste(ItemStack itemStack) {
        if (!itemStack.getOrCreateTag().contains(NBT_NAME)) {
            return null;
        }

        String toothpasteName = itemStack.getOrCreateTag().getString(NBT_NAME);
        if (toothpasteName.isEmpty()) {
            return null;
        }

        return BuiltInRegistries.ITEM.get(new ResourceLocation(toothpasteName));
    }

    /**
     * 将牙膏添加到 NBT 中
     * @param itemStack 牙刷
     * @param toothpaste 牙膏
     */
    public static void setToothpaste(ItemStack itemStack, Item toothpaste) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (toothpaste == null) {
            tag.putString(NBT_NAME, "");
        } else {
            tag.putString(NBT_NAME, BuiltInRegistries.ITEM.getKey(toothpaste).toString());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            /**
             * 自定义使用动画
             * @param poseStack    姿势
             * @param player       玩家
             * @param arm          拿东西的手
             * @param itemInHand   具体的物品
             * @param partialTick  一部分tick 用来插值使动画平滑
             * @param equipProcess 十字指针下面的剑型槽 用来画拿出来的动画
             * @param swingProcess 摆动时间
             * @return 是否直接渲染
             */
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                this.applyItemArmTransform(poseStack, arm, equipProcess);

                ItemStack toothpaste = player.getMainHandItem() == itemInHand ? player.getOffhandItem() : player.getMainHandItem();

                if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
                    if (getToothpaste(itemInHand) instanceof AbstractToothpaste) {
                        this.brushingArmTransform(poseStack, arm, player.getUseItemRemainingTicks(), itemInHand.getUseDuration(), partialTick);
                    } else if (toothpaste.getItem() instanceof AbstractToothpaste) {
                        this.applyToothpasteArmTransform(poseStack, arm, player.getUseItemRemainingTicks(), itemInHand.getUseDuration(), partialTick);
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

            /**
             * 挤牙膏的动作
             * @param poseStack 物品
             * @param arm 手
             */
            private void applyToothpasteArmTransform(PoseStack poseStack, HumanoidArm arm, int remainingDuration, int useDuration, float partialTick) {
                int i = arm == HumanoidArm.RIGHT ? 1 : -1;
                // 使用进度
                float progress = ((1 - ((float) remainingDuration / useDuration)) * 100) + partialTick;

                float angle = progress <= 20 ? i * progress * 1.5f : i * 30;
                float distanceX = progress <= 20 ? i * progress * -0.015f : i * -0.3f;

                poseStack.translate(distanceX, 0, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(angle));
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            }

            /**
             * 刷牙动作
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
                float angleX = progress <= 10 ? progress * 7.5f : 75;

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
