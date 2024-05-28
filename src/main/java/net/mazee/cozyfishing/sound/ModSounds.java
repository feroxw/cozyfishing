package net.mazee.cozyfishing.sound;

import net.mazee.cozyfishing.CozyFishing;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CozyFishing.MODID);


    public static final RegistryObject<SoundEvent> SOUND_FISH_HIT = registerSoundEvents("FishHit");
    public static final RegistryObject<SoundEvent> SOUND_FISH_BITE = registerSoundEvents("fishBite");
    public static final RegistryObject<SoundEvent> SOUND_JINGLE = registerSoundEvents("jingle1");
    public static final RegistryObject<SoundEvent> SOUND_SLOW_REEL = registerSoundEvents("slowReel");
    public static final RegistryObject<SoundEvent> SOUND_FAST_REEL = registerSoundEvents("fastReel");



    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CozyFishing.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
