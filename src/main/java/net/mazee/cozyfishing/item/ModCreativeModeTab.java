package net.mazee.cozyfishing.item;

import net.mazee.cozyfishing.CozyFishing;
import net.mazee.cozyfishing.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CozyFishing.MODID);

    public static final RegistryObject<CreativeModeTab> COZYFISHING_TAB = CREATIVE_MODE_TABS.register("cozyfishingtab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SDV_FISHING_ROD.get()))
                    .title(Component.translatable("itemGroup.cozyfishingtab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModItems.SDV_FISHING_ROD.get());

                        pOutput.accept(ModItems.CATFISH.get());
                        pOutput.accept(ModItems.HADDOCK.get());
                        pOutput.accept(ModItems.RED_SNAPPER.get());
                        pOutput.accept(ModItems.GREEN_SUNFISH.get());
                        pOutput.accept(ModItems.FANTAIL.get());
                        pOutput.accept(ModItems.GOLDFISH.get());

                        
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
