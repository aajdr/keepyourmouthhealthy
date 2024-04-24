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
        if (AbstractToothbrush.getToothpaste(toothbrush) instanceof AbstractToothpaste) {
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
     * @param poseStack 物品
     * @param hand 手
     */
    private static void applyToothpasteTransform(PoseStack poseStack, InteractionHand hand, int remainingDuration, int useDuration, float partialTick) {
        int i = hand == InteractionHand.MAIN_HAND ? 1 : -1;
        float progress = ((1 - ((float) remainingDuration / useDuration)) * 100) + partialTick;

        if (progress <= 20) {
            return;
        }

        float angle = progress <= 40 ? i * (progress - 20) * 1.5f : i * 30;
        float distanceX = progress <= 40 ? i * (progress - 20) * -0.018f : i * -0.36f;
        float distanceY = progress <= 40 ? (progress - 20) * -0.016f : -0.32f;
        float distanceZ = progress <= 40 ? (progress - 20) * 0.025f : 0.5f;

        poseStack.translate(distanceX, distanceY, distanceZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

        if (progress > 40) {
            poseStack.translate(i * (progress - 30) * 0.005, 0, 0);
        }
    }
}
