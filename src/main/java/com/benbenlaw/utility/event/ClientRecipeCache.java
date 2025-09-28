package com.benbenlaw.utility.event;

import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientRecipeCache {

    public static Map<ResourceLocation, DryingTableRecipe> cachedDryingTableRecipes = new HashMap<>();

    public static void setCachedDryingTableRecipes(Map<ResourceLocation, DryingTableRecipe> recipes) {
        cachedDryingTableRecipes = recipes;
    }

    public static Collection<DryingTableRecipe> getCachedDryingTableRecipes() {
        return cachedDryingTableRecipes.values();
    }

}
