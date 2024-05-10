package xyz.starchenpy.keepyourmouthhealthy.common.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.RedstoneToothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.WoodenToothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.*;

import java.util.Collection;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    // 牙刷
    public static final DeferredItem<Item> WOODEN_TOOTHBRUSH = ITEMS.registerItem("wooden_toothbrush", WoodenToothbrush::new, new Item.Properties().durability(160));
    public static final DeferredItem<Item> REDSTONE_TOOTHBRUSH = ITEMS.registerItem("redstone_toothbrush", RedstoneToothbrush::new, new Item.Properties().durability(200));

    // 牙膏
    public static final DeferredItem<Item> CHARCOAL_TOOTHPASTE = ITEMS.registerItem("charcoal_toothpaste", CharcoalToothpaste::new, new Item.Properties().durability(16));
    public static final DeferredItem<Item> FLINT_TOOTHPASTE = ITEMS.registerItem("flint_toothpaste", FlintToothpaste::new, new Item.Properties().durability(12));
    public static final DeferredItem<Item> QUARTZ_TOOTHPASTE = ITEMS.registerItem("quartz_toothpaste", QuartzToothpaste::new, new Item.Properties().durability(12));
    public static final DeferredItem<Item> REDSTONE_TOOTHPASTE = ITEMS.registerItem("redstone_toothpaste", RedstoneToothbrush::new, new Item.Properties().durability(12));
    public static final DeferredItem<Item> LAPIS_LAZULI_TOOTHPASTE = ITEMS.registerItem("lapis_lazuli_toothpaste", LapisLazuliToothpaste::new, new Item.Properties().durability(12));
    public static final DeferredItem<Item> GOLDEN_APPLE_TOOTHPASTE = ITEMS.registerItem("golden_apple_toothpaste", GoldenAppleToothpaste::new, new Item.Properties().durability(12));

    // 材料
    public static final DeferredItem<Item> BRISTLE = ITEMS.registerSimpleItem("bristle");
    public static final DeferredItem<Item> TOOTHBRUSH_HEAD = ITEMS.registerSimpleItem("toothbrush_head");

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getAllModItem() {
        return ITEMS.getEntries();
    }
}
