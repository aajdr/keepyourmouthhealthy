package xyz.starchenpy.keepyourmouthhealthy.common.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.AbstractToothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.AbstractToothpaste;
import xyz.starchenpy.keepyourmouthhealthy.common.util.MathUtil;
import xyz.starchenpy.keepyourmouthhealthy.common.util.NbtUtil;

public class ItemRenderListener {
    private static final Logger log = LoggerFactory.getLogger(ItemRenderListener.class);
    private static Matrix4f buffer;

    @SubscribeEvent
    public static void itemRenderListener(RenderHandEvent event) {
        // 因为主副手用的同一个 PoseStack, 对主手进行的姿态操作会导致副手物品也一起动
        // 将记录的未被修改的 PoseStack 覆盖给副手, 成功修复了此问题
        if (buffer != null && event.getHand() == InteractionHand.OFF_HAND) {
            event.getPoseStack().last().pose().set(buffer);
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack toothbrush = player.getUseItem();
        ItemStack toothpaste = event.getItemStack();

        if (!(toothpaste.getItem() instanceof AbstractToothpaste) || !(toothbrush.getItem() instanceof AbstractToothbrush)) {
            return;
        }
        // 牙刷上有牙膏说明正在刷牙，直接返回
        if (NbtUtil.getToothpaste(toothbrush) instanceof AbstractToothpaste) {
            return;
        }

        // 这里记录了主手未被修改过的 PoseStack
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            try {
                buffer = (Matrix4f) event.getPoseStack().last().pose().clone();
            } catch (CloneNotSupportedException e) {
                log.error(e.getMessage());
            }
        }

        int remainingDuration = player.getUseItemRemainingTicks();
        if (remainingDuration > 0) {
            applyToothpasteTransform(event.getPoseStack(), event.getHand(), remainingDuration, toothbrush.getUseDuration(), event.getPartialTick());
        }
    }

    /**
     * 挤牙膏的动作
     */
    private static void applyToothpasteTransform(PoseStack poseStack, InteractionHand hand, int remainingDuration, int useDuration, float partialTick) {
        int i = hand == InteractionHand.MAIN_HAND ? 1 : -1;
        float progress = 1 - ((remainingDuration - partialTick) / useDuration);

        if (progress <= 0.15) {
            return;
        }

        float nonlinear = MathUtil.easeOutQuint(Math.min(progress - 0.15f, 0.35f) / 0.35f, 3);

        float angle = i * nonlinear * 30;
        poseStack.translate(i * nonlinear * -0.32f, nonlinear * -0.32f, nonlinear * 0.4f);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

        if (progress > 0.5) {
            float progress2 = MathUtil.easeOutQuint((progress - 0.5f) / 0.5f, 2);
            poseStack.translate(i * progress2 * 0.3, 0, 0);
        }
    }
}
