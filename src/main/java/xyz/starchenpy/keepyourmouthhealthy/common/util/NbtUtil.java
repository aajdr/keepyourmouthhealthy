package xyz.starchenpy.keepyourmouthhealthy.common.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class NbtUtil {
    public static final String TOOTHBRUSH_TOOTHPASTE_NBT = "toothpaste";

    /**
     * 从 NBT 获取牙膏
     * @param itemStack 牙刷 ItemStack
     * @return 牙膏
     */
    public static Item getToothpaste(ItemStack itemStack) {
        if (!itemStack.getOrCreateTag().contains(TOOTHBRUSH_TOOTHPASTE_NBT)) {
            return null;
        }

        String toothpasteName = itemStack.getOrCreateTag().getString(TOOTHBRUSH_TOOTHPASTE_NBT);
        if (toothpasteName.isEmpty()) {
            return null;
        }

        return BuiltInRegistries.ITEM.get(new ResourceLocation(toothpasteName));
    }

    /**
     * 将牙膏添加到 NBT 中
     * @param itemStack 牙刷 ItemStack
     * @param toothpaste 牙膏
     */
    public static void setToothpaste(ItemStack itemStack, Item toothpaste) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (toothpaste == null) {
            tag.putString(TOOTHBRUSH_TOOTHPASTE_NBT, "");
        } else {
            tag.putString(TOOTHBRUSH_TOOTHPASTE_NBT, BuiltInRegistries.ITEM.getKey(toothpaste).toString());
        }
    }
}
