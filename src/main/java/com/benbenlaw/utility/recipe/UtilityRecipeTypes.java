package com.benbenlaw.utility.recipe;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class UtilityRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Utility.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Utility.MOD_ID);

    public static final Supplier<RecipeSerializer<DryingTableRecipe>> DRYING_TABLE_SERIALIZER =
            SERIALIZER.register("drying_table", () -> DryingTableRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<DryingTableRecipe>> DRYING_TABLE_TYPE =
            TYPES.register("drying_table", () -> DryingTableRecipe.Type.INSTANCE);


    public static final Supplier<RecipeSerializer<ResourceGeneratorRecipe>> RESOURCE_GENERATOR_SERIALIZER =
            SERIALIZER.register("resource_generator", () -> ResourceGeneratorRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<ResourceGeneratorRecipe>> RESOURCE_GENERATOR_TYPE =
            TYPES.register("resource_generator", () -> ResourceGeneratorRecipe.Type.INSTANCE);


}
