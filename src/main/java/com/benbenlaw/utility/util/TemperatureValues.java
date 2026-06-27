package com.benbenlaw.utility.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.StringRepresentable;

public enum TemperatureValues implements StringRepresentable {

    WARM,
    TEMPERATE,
    COLD;

    public static TemperatureValues fromString(String name) {
        return switch (name.toLowerCase()) {
            case "warm" -> WARM;
            case "temperate" -> TEMPERATE;
            case "cold" -> COLD;
            default -> throw new IllegalArgumentException("Unknown temperature: " + name);
        };
    }

    public static final Codec<TemperatureValues> CODEC = Codec.STRING
            .xmap(TemperatureValues::fromString, TemperatureValues::getSerializedName);

    public static void writeToBuffer(FriendlyByteBuf buffer, TemperatureValues temp) {
        buffer.writeEnum(temp);
    }

    public static TemperatureValues readFromBuffer(FriendlyByteBuf buffer) {
        return buffer.readEnum(TemperatureValues.class);
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
