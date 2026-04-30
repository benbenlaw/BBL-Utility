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

public record DryingTableRecipe(SizedIngredient input, ItemStackTemplate output, Optional<FluidStackTemplate> fluid, Optional<Integer> consumeAmount) implements Recipe<DryingTableRecipeInput> {

    public static final MapCodec<DryingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SizedIngredient.NESTED_CODEC.fieldOf("input").forGetter(DryingTableRecipe::input),
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(DryingTableRecipe::output),
                    FluidStackTemplate.CODEC.optionalFieldOf("fluid").forGetter(DryingTableRecipe::fluid),
                    Codec.INT.optionalFieldOf("consume_amount").forGetter(DryingTableRecipe::consumeAmount)
                    ).apply(instance, DryingTableRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DryingTableRecipe> STREAM_CODEC = StreamCodec.of(
            DryingTableRecipe::write, DryingTableRecipe::read);

    public static final RecipeType<DryingTableRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<DryingTableRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static DryingTableRecipe read(RegistryFriendlyByteBuf buffer) {
        SizedIngredient input = SizedIngredient.STREAM_CODEC.decode(buffer);
        ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

        Optional<FluidStackTemplate> fluid;
        if (buffer.readBoolean()) {
            fluid = Optional.of(FluidStackTemplate.STREAM_CODEC.decode(buffer));
        } else {
            fluid = Optional.empty();
        }

        Optional<Integer> consumeAmount;
        if (buffer.readBoolean()) {
            consumeAmount = Optional.of(buffer.readVarInt());
        } else {
            consumeAmount = Optional.empty();
        }


        return new DryingTableRecipe(input, output, fluid, consumeAmount);
    }

    private static void write(RegistryFriendlyByteBuf buffer, DryingTableRecipe recipe) {
        SizedIngredient.STREAM_CODEC.encode(buffer, recipe.input);
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        if (recipe.fluid.isPresent()) {
            buffer.writeBoolean(true);
            FluidStackTemplate.STREAM_CODEC.encode(buffer, recipe.fluid.get());
        } else {
            buffer.writeBoolean(false);
        }
        if  (recipe.consumeAmount.isPresent()) {
            buffer.writeBoolean(true);
            buffer.writeVarInt(recipe.consumeAmount.get());
        } else {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public boolean matches(@NotNull DryingTableRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) return false;

        ItemStack stack = recipeInput.getItem(DryingTableBlockEntity.INPUT_SLOT);
        if (stack.isEmpty()) return false;

        if (fluid.isPresent()) {
            if (fluid.get().is(recipeInput.getFluid().getFluidType()) && recipeInput.getFluid().getAmount() >= fluid.get().amount()) {
                return input.test(stack);
            } else {
                return false;
            }
        } else {
            return input.test(stack);
        }

    }

    //Boiler Plate
    @Override
    public @NonNull ItemStack assemble(DryingTableRecipeInput recipeInput) {
        return output.create().copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<DryingTableRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<DryingTableRecipeInput>> getType() {
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
