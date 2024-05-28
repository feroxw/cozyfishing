package net.mazee.cozyfishing.entity;

import net.mazee.cozyfishing.CozyFishing;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CozyFishing.MODID);
//    public static final RegistryObject<EntityType<SDVFishingHook>> SDV_FISHING_HOOK = register("sdv_fishing_hook", () -> EntityType.Builder.<SDVFishingHook>createNothing(MobCategory.MISC)
//            .noSave()
//            .noSummon()
//            .sized(0.25F, 0.25F)
//            .setTrackingRange(4)
//            .setUpdateInterval(5)
//            .setCustomClientFactory(SDVFishingHook::new));

    public static final RegistryObject<EntityType<SDVFishingHook>> SDV_FISHING_HOOK =
            ENTITIES.register("sdv_fishing_hook", () -> EntityType.Builder.<SDVFishingHook>createNothing(MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave().noSummon().sized(0.25F, 0.25F)
                    .setTrackingRange(4)
                    .setUpdateInterval(5)
                    .setCustomClientFactory(SDVFishingHook::new).build("sdv_fishing_hook"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
