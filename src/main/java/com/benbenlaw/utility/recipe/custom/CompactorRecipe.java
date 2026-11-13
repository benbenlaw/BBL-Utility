package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record CompactorRecipe(SizedIngredient input, ItemStackTemplate output) implements Recipe<SingleRecipeInput> {

    public static final MapCodec<CompactorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SizedIngredient.NESTED_CODEC.fieldOf("input").forGetter(CompactorRecipe::input),
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(CompactorRecipe::output)
                    ).apply(instance, CompactorRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CompactorRecipe> STREAM_CODEC = StreamCodec.of(
            CompactorRecipe::write, CompactorRecipe::read);

    public static final RecipeType<CompactorRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<CompactorRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static CompactorRecipe read(RegistryFriendlyByteBuf buffer) {
        SizedIngredient input = SizedIngredient.STREAM_CODEC.decode(buffer);
        ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

        return new CompactorRecipe(input, output);
    }

    private static void write(RegistryFriendlyByteBuf buffer, CompactorRecipe recipe) {
        SizedIngredient.STREAM_CODEC.encode(buffer, recipe.input);
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) return false;

        ItemStack stack = recipeInput.getItem(0);

        return input.test(stack);
    }

    //Boiler Plate
    @Override
    public @NonNull ItemStack assemble(SingleRecipeInput recipeInput) {
        return output.create().copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return TYPE;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }
}
