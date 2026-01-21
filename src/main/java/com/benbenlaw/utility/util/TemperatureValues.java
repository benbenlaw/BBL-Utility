package com.benbenlaw.utility.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

public enum TemperatureValues {

    WARM,
    TEMPERATE,
    COLD;

    public String asString() {
        return this.name().toLowerCase();
    }

    public static TemperatureValues fromString(String name) {
        return switch (name.toLowerCase()) {
            case "warm" -> WARM;
            case "temperate" -> TEMPERATE;
            case "cold" -> COLD;
            default -> throw new IllegalArgumentException("Unknown temperature: " + name);
        };
    }

    public static final Codec<TemperatureValues> CODEC = Codec.STRING
            .xmap(TemperatureValues::fromString, TemperatureValues::asString);

    public static void writeToBuffer(FriendlyByteBuf buffer, TemperatureValues temp) {
        buffer.writeEnum(temp);
    }

    // Read a TemperatureValues from a buffer
    public static TemperatureValues readFromBuffer(FriendlyByteBuf buffer) {
        return buffer.readEnum(TemperatureValues.class);
    }

}
