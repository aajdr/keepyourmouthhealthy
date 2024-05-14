package xyz.starchenpy.keepyourmouthhealthy.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy.MOD_ID;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    public static final Supplier<SoundEvent> BRUSHING_TEETH_SOUND = SOUNDS.register("brushing_teeth_sound", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(MOD_ID, "brushing_teeth_sound"),16));

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
