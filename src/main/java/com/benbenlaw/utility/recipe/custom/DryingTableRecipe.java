package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.benbenlaw.utility.recipe.DryingTableRecipeTypeCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

public record DryingTableRecipe(SizedIngredient input, ItemStack output, DryingTableRecipeType recipeType) implements Recipe<DryingTableRecipeInput> {

    @Override
    public boolean matches(@NotNull DryingTableRecipeInput recipeInput, Level level) {
        if (!level.isClientSide()) {

            if (recipeType == DryingTableRecipeType.DRYING && !recipeInput.isWaterlogged()) {
                return input.test(recipeInput.getItem(DryingTableBlockEntity.INPUT_SLOT));
            } else if (recipeType == DryingTableRecipeType.SOAKING && recipeInput.isWaterlogged()) {
                return input.test(recipeInput.getItem(DryingTableBlockEntity.INPUT_SLOT));
            }
        }

        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull DryingTableRecipeInput recipeInput, HolderLookup.@NotNull Provider provider) {
        return output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<DryingTableRecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<DryingTableRecipeInput>> getType() {
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

    public static class Type implements RecipeType<DryingTableRecipe> {
        private Type() {
        }

        public static final DryingTableRecipe.Type INSTANCE = new DryingTableRecipe.Type();
    }

    public static class Serializer implements RecipeSerializer<DryingTableRecipe> {
        public static final DryingTableRecipe.Serializer INSTANCE = new DryingTableRecipe.Serializer();

        public final MapCodec<DryingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        SizedIngredient.NESTED_CODEC.fieldOf("input").forGetter(DryingTableRecipe::input),
                        ItemStack.CODEC.fieldOf("output").forGetter(DryingTableRecipe::output),
                        DryingTableRecipeTypeCodec.CODEC.fieldOf("recipe_type").forGetter(DryingTableRecipe::recipeType)
                ).apply(instance, DryingTableRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, DryingTableRecipe> STREAM_CODEC = StreamCodec.of(
                DryingTableRecipe.Serializer::write, DryingTableRecipe.Serializer::read);

        @Override
        public @NotNull MapCodec<DryingTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, DryingTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static DryingTableRecipe read(RegistryFriendlyByteBuf buffer) {
            SizedIngredient input = SizedIngredient.STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            DryingTableRecipeType type = DryingTableRecipeTypeCodec.readFromBuffer(buffer);
            return new DryingTableRecipe(input, output, type);
        }

        private static void write(RegistryFriendlyByteBuf buffer, DryingTableRecipe recipe) {
            SizedIngredient.STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            DryingTableRecipeTypeCodec.writeToBuffer(buffer, recipe.recipeType);
        }
    }
}
