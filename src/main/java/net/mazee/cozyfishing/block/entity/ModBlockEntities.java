package net.mazee.cozyfishing.block.entity;

import net.mazee.cozyfishing.CozyFishing;
import net.mazee.cozyfishing.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CozyFishing.MODID);

    public static final RegistryObject<BlockEntityType<SDVFishingEntity>> SDV_FISHING_ENTITY =
            BLOCK_ENTITIES.register("sdv_fishing_entity", () ->
                    BlockEntityType.Builder.of(SDVFishingEntity::new,
                            ModBlocks.SDV_FISHING_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
