package xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
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
import xyz.starchenpy.keepyourmouthhealthy.common.ModSounds;
import xyz.starchenpy.keepyourmouthhealthy.common.advancements.ModTriggers;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.AbstractToothpaste;
import xyz.starchenpy.keepyourmouthhealthy.common.particle.ToothpasteParticleOption;
import xyz.starchenpy.keepyourmouthhealthy.common.util.MathUtil;
import xyz.starchenpy.keepyourmouthhealthy.common.util.NbtUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

public class AbstractToothbrush extends Item {
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
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.CUSTOM;
    }

    /**
     * 决定了刷牙所需的时间
     * @return 时间(tick)
     */
    @Override
    @ParametersAreNonnullByDefault
    public int getUseDuration(ItemStack itemStack) {
        if (NbtUtil.getToothpaste(itemStack) != null) {
            return useDuration;
        }

        return 40;
    }

    /**
     * 决定了刷牙后冷却时间
     * @return 冷却时间(tick)
     */
    public int getCooldown() {
        return 1200;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionHand offHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack toothbrush = player.getItemInHand(hand);
        ItemStack toothpaste = player.getItemInHand(offHand);

        // 刷牙或者抹牙膏
        if (toothpaste.getItem() instanceof AbstractToothpaste || NbtUtil.getToothpaste(toothbrush) instanceof AbstractToothpaste) {
            player.startUsingItem(hand);
            return InteractionResultHolder.pass(toothbrush);
        }

        return InteractionResultHolder.fail(toothbrush);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (NbtUtil.getToothpaste(itemStack) instanceof AbstractToothpaste toothpaste) {
            toothpaste.effect(entity);
            NbtUtil.setToothpaste(itemStack, null);
            if (entity instanceof ServerPlayer player) {
                player.getCooldowns().addCooldown(this, getCooldown());
                ModTriggers.AFTER_BRUSHING_TEETH.get().trigger(player, itemStack);
            }
            return itemStack;
        }

        ItemStack toothpasteOnHand = entity.getMainHandItem() == itemStack ? entity.getOffhandItem() : entity.getMainHandItem();
        if (toothpasteOnHand.getItem() instanceof AbstractToothpaste item) {
            toothpasteOnHand.hurtAndBreak(1, entity, (e) -> {});
            NbtUtil.setToothpaste(itemStack, item);
        }

        return itemStack;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide) {
            return;
        }

        if (!(NbtUtil.getToothpaste(stack) instanceof AbstractToothpaste)) {
            return;
        }

        // 生成粒子效果与音效
        if (shouldTriggerItemUseEffects(livingEntity, stack)) {
            this.spawnItemParticles(level, livingEntity, NbtUtil.getToothpaste(stack));
            livingEntity.playSound(ModSounds.BRUSHING_TEETH_SOUND.get(),
                    0.5F + 0.5F * (float)level.random.nextInt(2),
                    (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    private boolean shouldTriggerItemUseEffects(LivingEntity entity, ItemStack stack) {
        int i = entity.getUseItemRemainingTicks();
        boolean flag = i <= this.getUseDuration(stack) - (this.getUseDuration(stack) / 10);
        return flag && i % 4 == 0;
    }

    private void spawnItemParticles(Level level, LivingEntity entity, Item item) {
        for (int i = 0; i < 3; i++) {
            Vec3 speedVec3 = new Vec3(((double)level.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            speedVec3 = speedVec3.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
            speedVec3 = speedVec3.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));

            double d0 = (double)(-level.random.nextFloat()) * 0.6 - 0.3;
            Vec3 posVec3 = new Vec3(((double)level.random.nextFloat() - 0.5) * 0.3, d0, 0.6);
            posVec3 = posVec3.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
            posVec3 = posVec3.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
            posVec3 = posVec3.add(entity.getX(), entity.getEyeY(), entity.getZ());

            ItemStack stack = new ItemStack(item);
            level.addParticle(new ToothpasteParticleOption(stack), posVec3.x, posVec3.y, posVec3.z, speedVec3.x, speedVec3.y + 0.05, speedVec3.z);
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
             */
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                this.applyItemArmTransform(poseStack, arm, equipProcess);

                ItemStack toothpaste = player.getMainHandItem() == itemInHand ? player.getOffhandItem() : player.getMainHandItem();

                if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
                    if (NbtUtil.getToothpaste(itemInHand) instanceof AbstractToothpaste) {
                        this.brushingArmTransform(poseStack, arm, player.getUseItemRemainingTicks(), itemInHand.getUseDuration(), partialTick);
                    } else if (toothpaste.getItem() instanceof AbstractToothpaste) {
                        this.applyToothpasteArmTransform(poseStack, arm, player.getUseItemRemainingTicks(), itemInHand.getUseDuration(), partialTick);
                    }
                }

                return true;
            }

            /**
             * 物品取出时的上抬动作
             */
            private void applyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equipProcess) {
                int i = hand == HumanoidArm.RIGHT ? 1 : -1;
                poseStack.translate((float)i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
            }

            /**
             * 挤牙膏的动作
             */
            private void applyToothpasteArmTransform(PoseStack poseStack, HumanoidArm arm, int remainingDuration, int useDuration, float partialTick) {
                int i = arm == HumanoidArm.RIGHT ? 1 : -1;
                // 使用进度
                float progress = MathUtil.easeOutQuint(1 - (remainingDuration - partialTick) / useDuration, 20);
                poseStack.translate(i * progress * -0.3f, 0, 0);
                float angle = i * progress * 30;
                poseStack.mulPose(Axis.YP.rotationDegrees(angle));
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            }

            /**
             * 刷牙动作
             */
            private void brushingArmTransform(PoseStack poseStack, HumanoidArm arm, int remainingDuration, int useDuration, float partialTick) {
                int i = arm == HumanoidArm.RIGHT ? 1 : -1;
                // 使用进度
                float progress = 1 - ((remainingDuration - partialTick) / useDuration);
                // 摆动幅度
                double amplitude = i * Math.sin(remainingDuration - partialTick) / 4;

                float nonlinear = MathUtil.easeOutQuint(progress, 50);
                poseStack.mulPose(Axis.ZP.rotationDegrees(i * nonlinear * 90));
                poseStack.mulPose(Axis.XP.rotationDegrees(nonlinear * 75));

                // 根据进度调整位置
                if (progress <= 0.1) {
                    poseStack.translate(0, 0, -0.1);
                } else if (progress <= 0.4) {
                    poseStack.translate(0, 0, amplitude);
                } else if (progress <= 0.7) {
                    poseStack.translate(amplitude / 3, 0, 0);
                } else {
                    poseStack.translate(0, 0, amplitude);
                }
            }
        });
    }
}
