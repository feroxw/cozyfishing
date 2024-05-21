package net.mazee.cozyfishing.item;

import net.mazee.cozyfishing.CozyFishing;
import net.mazee.cozyfishing.item.custom.StardewFishingRodItem;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CozyFishing.MODID);

    public static final RegistryObject<Item> SDV_FISHING_ROD = ITEMS.register("sdv_fishing_rod",
            () -> new StardewFishingRodItem(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
