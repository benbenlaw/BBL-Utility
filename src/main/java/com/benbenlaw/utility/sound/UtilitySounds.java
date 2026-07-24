package com.benbenlaw.utility.sound;

import com.benbenlaw.utility.Utility;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class UtilitySounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Utility.MOD_ID);

    public static final Identifier DOORBELL_ID = Utility.identifier("doorbell");
    public static final Supplier<SoundEvent> DOORBELL = SOUND_EVENTS.register("doorbell", () -> SoundEvent.createVariableRangeEvent(DOORBELL_ID));
}
