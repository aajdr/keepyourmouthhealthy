package xyz.starchenpy.keepyourmouthhealthy.common.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.RedstoneToothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothbrush.Toothbrush;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.CharcoalToothpaste;
import xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste.FlintToothpaste;

import java.util.Collection;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> TOOTHBRUSH = ITEMS.registerItem("toothbrush", Toothbrush::new, new Item.Properties().durability(200));
    public static final DeferredItem<Item> REDSTONE_TOOTHBRUSH = ITEMS.registerItem("redstone_toothbrush", RedstoneToothbrush::new, new Item.Properties().durability(400));
    public static final DeferredItem<Item> CHARCOAL_TOOTHPASTE = ITEMS.registerItem("charcoal_toothpaste", CharcoalToothpaste::new, new Item.Properties().durability(16));
    public static final DeferredItem<Item> FLINT_TOOTHPASTE = ITEMS.registerItem("flint_toothpaste", FlintToothpaste::new, new Item.Properties().durability(12));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getAllModItem() {
        return ITEMS.getEntries();
    }
}
