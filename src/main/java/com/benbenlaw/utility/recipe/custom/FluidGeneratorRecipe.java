package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.FluidGeneratorBlockEntity;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record FluidGeneratorRecipe(String input, FluidStack output) implements Recipe<RecipeInput> {

    public static final MapCodec<FluidGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("input").forGetter(FluidGeneratorRecipe::input),
                    FluidStack.CODEC.fieldOf("output").forGetter(FluidGeneratorRecipe::output)
            ).apply(instance, FluidGeneratorRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
            FluidGeneratorRecipe::write, FluidGeneratorRecipe::read);

    public static final RecipeType<FluidGeneratorRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<FluidGeneratorRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static FluidGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
        String input = ByteBufCodecs.STRING_UTF8.decode(buffer);
        FluidStack output = FluidStack.STREAM_CODEC.decode(buffer);
        return new FluidGeneratorRecipe(input, output);
    }

    private static void write(RegistryFriendlyByteBuf buffer, FluidGeneratorRecipe recipe) {
        ByteBufCodecs.STRING_UTF8.encode(buffer, recipe.input);
        FluidStack.STREAM_CODEC.encode(buffer, recipe.output);
    }

    @Override
    public boolean matches(@NotNull RecipeInput recipeInput, Level level) {
        if (level.isClientSide()) return false;

        ItemStack stack = recipeInput.getItem(FluidGeneratorBlockEntity.INPUT_SLOT);

        if (stack.isEmpty()) return false;

        FluidStack fluidInStack = FluidUtil.getFirstStackContained(stack);
        Optional<Fluid> fluidInTank = BuiltInRegistries.FLUID.getOptional(Identifier.parse(input));

        return fluidInTank.filter(fluid -> fluidInStack.getFluid() == fluid).isPresent();

    }

    //Boiler Plate
    @Override
    public @NonNull ItemStack assemble(RecipeInput recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
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
