package xyz.starchenpy.keepyourmouthhealthy.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import xyz.starchenpy.keepyourmouthhealthy.common.item.ModItems;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.AbstractToothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.AbstractToothpaste;
import xyz.starchenpy.keepyourmouthhealthy.common.util.NbtUtil;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class PropertyRegistry {

    /**
     * 为牙刷添加 property, 以控制牙膏显示与否
     */
    @SubscribeEvent
    public static void propertyOverrideRegistry(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            for (DeferredHolder<Item, ? extends Item> holder : ModItems.getAllModItem()) {
                if (holder.get().asItem() instanceof AbstractToothbrush toothbrush) {
                    ItemProperties.register(toothbrush, new ResourceLocation(MOD_ID,"has_toothpaste"),
                            (stack, level, player, send)-> NbtUtil.getToothpaste(stack) instanceof AbstractToothpaste ? 1 : 0);

                }
            }
        });
    }
}