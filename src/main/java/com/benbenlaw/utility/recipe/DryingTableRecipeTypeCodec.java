package com.benbenlaw.utility.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.ClickType;

import java.util.Locale;

public class DryingTableRecipeTypeCodec {

    public static final Codec<DryingTableRecipeType> CODEC = Codec.STRING.xmap(
            s -> switch (s.toLowerCase(Locale.ROOT)) {
                case "drying" -> DryingTableRecipeType.DRYING;
                case "soaking" -> DryingTableRecipeType.SOAKING;
                default -> throw new IllegalArgumentException("Unknown recipe recipeType: " + s);
            }, DryingTableRecipeType::toString

    );

    public static void writeToBuffer(RegistryFriendlyByteBuf buffer, DryingTableRecipeType type) {
        buffer.writeEnum(type);
    }

    public static DryingTableRecipeType readFromBuffer(RegistryFriendlyByteBuf buffer) {
        return buffer.readEnum(DryingTableRecipeType.class);
    }
}
