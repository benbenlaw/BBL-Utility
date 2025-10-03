package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.FluidGeneratorBlockEntity;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import com.benbenlaw.utility.recipe.ResourceGeneratorRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record FluidGeneratorRecipe(String input, FluidStack output) implements Recipe<RecipeInput> {

    @Override
    public boolean matches(@NotNull RecipeInput recipeInput, Level level) {
        if (level.isClientSide()) return false;

        ItemStack stack = recipeInput.getItem(FluidGeneratorBlockEntity.INPUT_SLOT);
        if (stack.isEmpty()) return false;

        //var fluidHandler = Capabilities.Fluid.ITEM.getCapability(stack, null);
//
        //if (fluidHandler != null && fluidHandler.size() > 0) {
        //    FluidResource res = fluidHandler.getResource(0);
        //    long amount = fluidHandler.getAmountAsLong(0);
//
        //    FluidStack inputFluid = new FluidStack(res.getFluid(), (int) amount);
//
//
        //    if (!inputFluid.isEmpty()) {
        //        return inputFluid.getFluid() == output.getFluid();
        //    }
        //}
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput recipeInput, HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
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

    public static class Type implements RecipeType<FluidGeneratorRecipe> {
        private Type() {
        }

        public static final FluidGeneratorRecipe.Type INSTANCE = new FluidGeneratorRecipe.Type();
    }

    public static class Serializer implements RecipeSerializer<FluidGeneratorRecipe> {
        public static final FluidGeneratorRecipe.Serializer INSTANCE = new FluidGeneratorRecipe.Serializer();

        public final MapCodec<FluidGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("input").forGetter(FluidGeneratorRecipe::input),
                        FluidStack.CODEC.fieldOf("output").forGetter(FluidGeneratorRecipe::output)
                ).apply(instance, FluidGeneratorRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, FluidGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
                FluidGeneratorRecipe.Serializer::write, FluidGeneratorRecipe.Serializer::read);

        @Override
        public @NotNull MapCodec<FluidGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FluidGeneratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static FluidGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
            String input = ByteBufCodecs.STRING_UTF8.decode(buffer);
            FluidStack output = FluidStack.STREAM_CODEC.decode(buffer);
            return new FluidGeneratorRecipe(input, output);
        }

        private static void write(RegistryFriendlyByteBuf buffer, FluidGeneratorRecipe recipe) {
            ByteBufCodecs.STRING_UTF8.encode(buffer, recipe.input);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
