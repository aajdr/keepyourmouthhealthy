package xyz.starchenpy.keepyourmouthhealthy.common.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.RedstoneToothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.Toothbrush;

import java.util.function.Function;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> TOOTHBRUSH = ITEMS.registerItem("toothbrush", Toothbrush::new, new Item.Properties().durability(200));
    public static final DeferredItem<Item> REDSTONE_TOOTHBRUSH = ITEMS.registerItem("redstone_toothbrush", RedstoneToothbrush::new, new Item.Properties().durability(400));
    public static final DeferredItem<Item> TOOTHPASTE = ITEMS.registerItem("toothpaste", Toothpaste::new, new Item.Properties().defaultDurability(32).setNoRepair());
    public static final DeferredItem<Item> POWERFUL_ABRASIVE_PASTE = ITEMS.registerItem("powerful_abrasive_paste", Toothpaste::new, new Item.Properties().defaultDurability(16).setNoRepair());

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
