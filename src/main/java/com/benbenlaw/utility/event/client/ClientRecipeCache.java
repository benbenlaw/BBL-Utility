package com.benbenlaw.utility.event.client;

import com.benbenlaw.utility.recipe.custom.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.*;

public class ClientRecipeCache {

    //Drying Table Recipe Cache
    public static Map<Identifier, DryingTableRecipe> cachedDryingTableRecipes = new HashMap<>();

    public static void setCachedDryingTableRecipes(Map<Identifier, DryingTableRecipe> recipes) {
        cachedDryingTableRecipes = recipes;
    }

    public static Collection<ResourceGeneratorRecipe> getCachedResourceGeneratorRecipes() {
        return cachedResourceGeneratorRecipes.values();
    }

    //Resource Generator Recipe Cache
    public static Map<Identifier, ResourceGeneratorRecipe> cachedResourceGeneratorRecipes = new HashMap<>();

    public static Collection<DryingTableRecipe> getCachedDryingTableRecipes() {
        return cachedDryingTableRecipes.values();
    }

    public static void setCachedResourceGeneratorRecipes(Map<Identifier, ResourceGeneratorRecipe> recipes) {
        cachedResourceGeneratorRecipes = recipes;
    }

    //Fluid Generator Recipe Cache
    public static Map<Identifier, FluidGeneratorRecipe> cachedFluidGeneratorRecipes = new HashMap<>();

    public static Collection<FluidGeneratorRecipe> getCachedFluidGeneratorRecipes() {
        return cachedFluidGeneratorRecipes.values();
    }

    public static void setCachedFluidGeneratorRecipes(Map<Identifier, FluidGeneratorRecipe> recipes) {
        cachedFluidGeneratorRecipes = recipes;
    }

    //Summoning Recipe Cache
    public static Map<Identifier, SummoningRecipe> cachedSummoningRecipes = new HashMap<>();

    public static Collection<SummoningRecipe> getCachedSummoningRecipes() {
        return cachedSummoningRecipes.values();
    }

    public static void setCachedSummoningRecipes(Map<Identifier, SummoningRecipe> recipes) {
        cachedSummoningRecipes = recipes;
    }
}
