package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.benbenlaw.utility.recipe.DryingTableRecipeTypeCodec;
import com.benbenlaw.utility.recipe.ResourceGeneratorRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public record ResourceGeneratorRecipe(ItemStack input, ItemStack output, FluidStack leftFluid, FluidStack rightFluid, boolean consumeLeft, boolean consumeRight) implements Recipe<ResourceGeneratorRecipeInput> {

    @Override
    public boolean matches(@NotNull ResourceGeneratorRecipeInput recipeInput, Level level) {
        if (!level.isClientSide()) {

            boolean inputMatches = input().is(recipeInput.getItem(ResourceGeneratorBlockEntity.INPUT_SLOT).getItem());
            boolean leftFluidMatches = leftFluid.is(recipeInput.getLeftFluid().getFluid()) && leftFluid.getAmount() <= recipeInput.getLeftFluid().getAmount();
            boolean rightFluidMatches = rightFluid.is(recipeInput.getRightFluid().getFluid()) && rightFluid.getAmount() <= recipeInput.getRightFluid().getAmount();

            return inputMatches && leftFluidMatches && rightFluidMatches;
        }

        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull ResourceGeneratorRecipeInput recipeInput, HolderLookup.@NotNull Provider provider) {
        return output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<ResourceGeneratorRecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<ResourceGeneratorRecipeInput>> getType() {
        return Type.INSTANCE;
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

    public static class Type implements RecipeType<ResourceGeneratorRecipe> {
        private Type() {
        }

        public static final ResourceGeneratorRecipe.Type INSTANCE = new ResourceGeneratorRecipe.Type();
    }

    public static class Serializer implements RecipeSerializer<ResourceGeneratorRecipe> {
        public static final ResourceGeneratorRecipe.Serializer INSTANCE = new ResourceGeneratorRecipe.Serializer();

        public final MapCodec<ResourceGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("input").forGetter(ResourceGeneratorRecipe::input),
                        ItemStack.CODEC.fieldOf("output").forGetter(ResourceGeneratorRecipe::output),
                        FluidStack.CODEC.fieldOf("left_fluid").forGetter(ResourceGeneratorRecipe::leftFluid),
                        FluidStack.CODEC.fieldOf("right_fluid").forGetter(ResourceGeneratorRecipe::rightFluid),
                        Codec.BOOL.fieldOf("consume_left").forGetter(ResourceGeneratorRecipe::consumeLeft),
                        Codec.BOOL.fieldOf("consume_right").forGetter(ResourceGeneratorRecipe::consumeRight)
                ).apply(instance, ResourceGeneratorRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, ResourceGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
                ResourceGeneratorRecipe.Serializer::write, ResourceGeneratorRecipe.Serializer::read);

        @Override
        public @NotNull MapCodec<ResourceGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ResourceGeneratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ResourceGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
            ItemStack input = ItemStack.STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            FluidStack leftFluid = FluidStack.STREAM_CODEC.decode(buffer);
            FluidStack rightFluid = FluidStack.STREAM_CODEC.decode(buffer);
            boolean consumeLeft = buffer.readBoolean();
            boolean consumeRight = buffer.readBoolean();
            return new ResourceGeneratorRecipe(input, output, leftFluid, rightFluid, consumeLeft, consumeRight);
        }

        private static void write(RegistryFriendlyByteBuf buffer, ResourceGeneratorRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.leftFluid);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.rightFluid);
            buffer.writeBoolean(recipe.consumeLeft);
            buffer.writeBoolean(recipe.consumeRight);;
        }
    }
}
