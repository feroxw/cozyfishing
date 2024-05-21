package net.mazee.cozyfishing.event;

import net.mazee.cozyfishing.CozyFishing;
import net.mazee.cozyfishing.block.entity.ModBlockEntities;
import net.mazee.cozyfishing.block.entity.renderer.SDVFishingEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = CozyFishing.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {

        }
    }

    @Mod.EventBusSubscriber(modid = CozyFishing.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.SDV_FISHING_ENTITY.get(),
                    SDVFishingEntityRenderer::new);
        }


    }
}
