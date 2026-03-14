package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.benbenlaw.utility.recipe.DryingTableRecipeTypeCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record DryingTableRecipe(SizedIngredient input, ItemStackTemplate output, DryingTableRecipeType recipeType) implements Recipe<DryingTableRecipeInput> {

    public static final MapCodec<DryingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SizedIngredient.NESTED_CODEC.fieldOf("input").forGetter(DryingTableRecipe::input),
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(DryingTableRecipe::output),
                    DryingTableRecipeTypeCodec.CODEC.fieldOf("recipe_type").forGetter(DryingTableRecipe::recipeType)
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
        DryingTableRecipeType type = DryingTableRecipeTypeCodec.readFromBuffer(buffer);
        return new DryingTableRecipe(input, output, type);
    }

    private static void write(RegistryFriendlyByteBuf buffer, DryingTableRecipe recipe) {
        SizedIngredient.STREAM_CODEC.encode(buffer, recipe.input);
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        DryingTableRecipeTypeCodec.writeToBuffer(buffer, recipe.recipeType);
    }

    @Override
    public boolean matches(@NotNull DryingTableRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) return false;

        ItemStack stack = recipeInput.getItem(DryingTableBlockEntity.INPUT_SLOT);
        if (stack.isEmpty()) return false;

        if (recipeType == DryingTableRecipeType.DRYING && !recipeInput.isWaterlogged()) {
            return input.test(stack);
        }

        if (recipeType == DryingTableRecipeType.SOAKING && recipeInput.isWaterlogged()) {
            return input.test(stack);
        }

        return false;
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
