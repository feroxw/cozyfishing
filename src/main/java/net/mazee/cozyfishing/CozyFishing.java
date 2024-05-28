package net.mazee.cozyfishing;

import com.mojang.logging.LogUtils;
import net.mazee.cozyfishing.block.ModBlocks;
import net.mazee.cozyfishing.block.entity.ModBlockEntities;
import net.mazee.cozyfishing.data.Fish;
import net.mazee.cozyfishing.entity.ModEntities;
import net.mazee.cozyfishing.item.ModCreativeModeTab;
import net.mazee.cozyfishing.item.ModItems;
import net.mazee.cozyfishing.loot.ModLootModifiers;
import net.mazee.cozyfishing.network.PacketHandler;
import net.mazee.cozyfishing.renderers.SDVFishingHookRenderer;
import net.mazee.cozyfishing.screen.ModMenuTypes;
import net.mazee.cozyfishing.screen.SDVFishingScreen;
import net.mazee.cozyfishing.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CozyFishing.MODID)
public class CozyFishing
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cozyfishing";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    public CozyFishing()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);

        ModMenuTypes.register(modEventBus);
        ModLootModifiers.register(modEventBus);

        ModCreativeModeTab.register(modEventBus);

        PacketHandler.register();

        ModSounds.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        //LOGGER.info("HELLO FROM COMMON SETUP");
        //System.out.println("COMMON SETUP");
        Fish.main();

        event.enqueueWork(() -> {
            //PacketHandler.register();
            //ModVillagers.registerPOIs();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {

        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        //LOGGER.info("HELLO from server starting");
    }

//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public void onFished(ItemFishedEvent event) {
//
//
//        Player player = event.getEntity();
//
//        System.out.println("I fished!");
//
//        if(player.getInventory().getSelected().is(ModItems.SDV_FISHING_ROD.get())){
//            System.out.println(event.getEntity().getInventory().getSelected());
//            System.out.println(player.level().isClientSide());
//            //event.setCanceled(true);
//            List<ItemStack> lootList = event.getDrops();
//            float catchChance = 1f;
//            float variability = 0f;
//            for(ItemStack itemStack: lootList) {
//
//            }
//            catchChance += (float) (variability * 2 * (event.getEntity().getRandom().nextFloat() - 0.5));
//
//            if(player.level().isClientSide()){
//                Minecraft.getInstance().setScreen(new SDVFishingScreen(Component.literal("FISHING"), player));
//            }
//        }
//
//        //PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new MinigamePacket(catchChance,  event.getHookEntity().position()));
//        //CommonProxy.CURRENTLY_PLAYING.put(event.getEntity().getUUID(), lootList);
//    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

              //MenuScreens.register(ModMenuTypes.SDV_FISHING_MENU.get(), SDVFishingScreen::new);

            
        }

        @SubscribeEvent
        public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.SDV_FISHING_HOOK.get(), FishingHookRenderer::new);
        }



        @SubscribeEvent
        public static void registerLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {

        }

        @SubscribeEvent
        public static void addLayers(final EntityRenderersEvent.AddLayers event) {
            event.getSkins().forEach(name -> {
                if(event.getSkin(name) instanceof PlayerRenderer renderer) {

                }
            });
            if(event.getRenderer(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer renderer) {

            }
        
        }
    }
}
