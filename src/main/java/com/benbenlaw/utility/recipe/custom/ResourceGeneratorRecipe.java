package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.ResourceGeneratorRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record ResourceGeneratorRecipe(ItemStackTemplate input, ItemStackTemplate output, FluidStack leftFluid, FluidStack rightFluid, boolean consumeLeft, boolean consumeRight) implements Recipe<ResourceGeneratorRecipeInput> {

    public static final MapCodec<ResourceGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ItemStackTemplate.CODEC.fieldOf("input").forGetter(ResourceGeneratorRecipe::input),
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(ResourceGeneratorRecipe::output),
                    FluidStack.CODEC.fieldOf("left_fluid").forGetter(ResourceGeneratorRecipe::leftFluid),
                    FluidStack.CODEC.fieldOf("right_fluid").forGetter(ResourceGeneratorRecipe::rightFluid),
                    Codec.BOOL.fieldOf("consume_left").forGetter(ResourceGeneratorRecipe::consumeLeft),
                    Codec.BOOL.fieldOf("consume_right").forGetter(ResourceGeneratorRecipe::consumeRight)
            ).apply(instance, ResourceGeneratorRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
            ResourceGeneratorRecipe::write, ResourceGeneratorRecipe::read);

    public static final RecipeType<ResourceGeneratorRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<ResourceGeneratorRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static ResourceGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
        ItemStackTemplate input = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        FluidStack leftFluid = FluidStack.STREAM_CODEC.decode(buffer);
        FluidStack rightFluid = FluidStack.STREAM_CODEC.decode(buffer);
        boolean consumeLeft = buffer.readBoolean();
        boolean consumeRight = buffer.readBoolean();
        return new ResourceGeneratorRecipe(input, output, leftFluid, rightFluid, consumeLeft, consumeRight);
    }

    private static void write(RegistryFriendlyByteBuf buffer, ResourceGeneratorRecipe recipe) {
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.input);
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        FluidStack.STREAM_CODEC.encode(buffer, recipe.leftFluid);
        FluidStack.STREAM_CODEC.encode(buffer, recipe.rightFluid);
        buffer.writeBoolean(recipe.consumeLeft);
        buffer.writeBoolean(recipe.consumeRight);;
    }

    @Override
    public boolean matches(@NotNull ResourceGeneratorRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) return false;

        boolean inputMatches = input().is(recipeInput.getItem(ResourceGeneratorBlockEntity.INPUT_SLOT).getItem());

        var leftTank = recipeInput.getLeftFluid();
        var rightTank = recipeInput.getRightFluid();

        boolean defaultOrder = leftFluid.is(leftTank.getFluid()) && leftFluid.getAmount() <= leftTank.getAmount() && rightFluid.is(rightTank.getFluid()) && rightFluid.getAmount() <= rightTank.getAmount();
        boolean swappedOrder = leftFluid.is(rightTank.getFluid()) && leftFluid.getAmount() <= rightTank.getAmount() && rightFluid.is(leftTank.getFluid()) && rightFluid.getAmount() <= leftTank.getAmount();

        return inputMatches && (defaultOrder || swappedOrder);
    }

    //Boiler Plate
    @Override
    public @NonNull ItemStack assemble(ResourceGeneratorRecipeInput recipeInput) {
        return output.create().copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<ResourceGeneratorRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<ResourceGeneratorRecipeInput>> getType() {
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
