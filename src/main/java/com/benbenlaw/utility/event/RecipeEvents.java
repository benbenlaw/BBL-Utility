package com.benbenlaw.utility.event;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.nimbusds.jose.util.Resource;
import net.minecraft.resources.ResourceLocation;
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
    }

    @SubscribeEvent
    public static void onRecipeReceived(RecipesReceivedEvent event) {
        RecipeMap recipeMap = event.getRecipeMap();

        Collection<RecipeHolder<DryingTableRecipe>> dryingTableRecipes = recipeMap.byType(UtilityRecipeTypes.DRYING_TABLE_TYPE.get());
        Collection<RecipeHolder<ResourceGeneratorRecipe>> resourceGeneratorRecipes = recipeMap.byType(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get());


        Map<ResourceLocation, DryingTableRecipe> dryingTableRecipeMap = new HashMap<>();
        Map<ResourceLocation, ResourceGeneratorRecipe> resourceGeneratorRecipeMap = new HashMap<>();

        for (RecipeHolder<DryingTableRecipe> holder : dryingTableRecipes) {
            dryingTableRecipeMap.put(holder.id().location(), holder.value());
        }

        for (RecipeHolder<ResourceGeneratorRecipe> holder : resourceGeneratorRecipes) {
            resourceGeneratorRecipeMap.put(holder.id().location(), holder.value());
        }

        ClientRecipeCache.setCachedDryingTableRecipes(dryingTableRecipeMap);
        ClientRecipeCache.setCachedResourceGeneratorRecipes(resourceGeneratorRecipeMap);

    }
}
