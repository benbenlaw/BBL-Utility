package com.benbenlaw.utility.event;

import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientRecipeCache {

    //Drying Table Recipe Cache
    public static Map<ResourceLocation, DryingTableRecipe> cachedDryingTableRecipes = new HashMap<>();

    public static void setCachedDryingTableRecipes(Map<ResourceLocation, DryingTableRecipe> recipes) {
        cachedDryingTableRecipes = recipes;
    }

    public static Collection<ResourceGeneratorRecipe> getCachedResourceGeneratorRecipes() {
        return cachedResourceGeneratorRecipes.values();
    }

    //Resource Generator Recipe Cache
    public static Map<ResourceLocation, ResourceGeneratorRecipe> cachedResourceGeneratorRecipes = new HashMap<>();

    public static Collection<DryingTableRecipe> getCachedDryingTableRecipes() {
        return cachedDryingTableRecipes.values();
    }

    public static void setCachedResourceGeneratorRecipes(Map<ResourceLocation, ResourceGeneratorRecipe> recipes) {
        cachedResourceGeneratorRecipes = recipes;
    }



}
