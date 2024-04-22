package xyz.starchenpy.keepyourmouthhealthy.common.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.AbstractToothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.AbstractToothpaste;

public class ItemRenderListener {
    @SubscribeEvent
    public static void itemRenderListener(RenderHandEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        int remainingDuration = player.getUseItemRemainingTicks();
        ItemStack toothbrush = player.getMainHandItem();
        ItemStack toothpaste = player.getOffhandItem();

        if (AbstractToothbrush.getToothpaste(toothbrush) instanceof AbstractToothpaste) {
            return;
        }

        if (toothpaste.getItem() instanceof AbstractToothpaste && remainingDuration > 0) {
            PoseStack poseStack = event.getPoseStack();

            int i = event.getHand() == InteractionHand.MAIN_HAND ? 1 : -1;
            float progress = ((1 - ((float) remainingDuration / toothbrush.getUseDuration())) * 100) + event.getPartialTick();

            if (progress <= 20) {
                return;
            }

            float angle = progress <= 40 ? i * (progress - 20) * 1.5f : i * 30;
            float distanceX = progress <= 40 ? i * (progress - 20) * -0.012f : i * -0.24f;
            float distanceY = progress <= 40 ? (progress - 20) * -0.015f : -0.3f;

            poseStack.translate(distanceX, distanceY, 0.1f);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

            if (progress > 40) {
                poseStack.translate((progress - 30) * -0.005, 0, 0);
            }
        }
    }
}
