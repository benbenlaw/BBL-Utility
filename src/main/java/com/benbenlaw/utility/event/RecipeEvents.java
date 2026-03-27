package com.benbenlaw.utility.event;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.event.client.ClientRecipeCache;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.recipe.custom.FluidGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.SummoningRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class RecipeEvents {

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(UtilityRecipeTypes.DRYING_TABLE_TYPE.get());
        event.sendRecipes(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get());
        event.sendRecipes(UtilityRecipeTypes.FLUID_GENERATOR_TYPE.get());
        event.sendRecipes(UtilityRecipeTypes.SUMMONING_TYPE.get());
    }

    @SubscribeEvent
    public static void onRecipeReceived(RecipesReceivedEvent event) {
        RecipeMap recipeMap = event.getRecipeMap();

        //Drying Table Recipes
        Collection<RecipeHolder<DryingTableRecipe>> dryingTableRecipes = recipeMap.byType(UtilityRecipeTypes.DRYING_TABLE_TYPE.get());
        Map<Identifier, DryingTableRecipe> dryingTableRecipeMap = new HashMap<>();

        for (RecipeHolder<DryingTableRecipe> holder : dryingTableRecipes) {
            dryingTableRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedDryingTableRecipes(dryingTableRecipeMap);

        //Resource Generator Recipes
        Collection<RecipeHolder<ResourceGeneratorRecipe>> resourceGeneratorRecipes = recipeMap.byType(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get());
        Map<Identifier, ResourceGeneratorRecipe> resourceGeneratorRecipeMap = new HashMap<>();

        for (RecipeHolder<ResourceGeneratorRecipe> holder : resourceGeneratorRecipes) {
            resourceGeneratorRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedResourceGeneratorRecipes(resourceGeneratorRecipeMap);

        //Fluid Generator Recipes
        Collection<RecipeHolder<FluidGeneratorRecipe>> fluidGeneratorRecipes = recipeMap.byType(UtilityRecipeTypes.FLUID_GENERATOR_TYPE.get());
        Map<Identifier, FluidGeneratorRecipe> fluidGeneratorRecipeMap = new HashMap<>();

        for (RecipeHolder<FluidGeneratorRecipe> holder : fluidGeneratorRecipes) {
            fluidGeneratorRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedFluidGeneratorRecipes(fluidGeneratorRecipeMap);

        //Summoning Recipes
        Collection<RecipeHolder<SummoningRecipe>> summoningRecipes = recipeMap.byType(UtilityRecipeTypes.SUMMONING_TYPE.get());
        Map<Identifier, SummoningRecipe> summoningRecipeMap = new HashMap<>();

        for (RecipeHolder<SummoningRecipe> holder : summoningRecipes) {
            summoningRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedSummoningRecipes(summoningRecipeMap);

    }
}
