package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.core.integration.jei.GhostFilter;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.event.ClientRecipeCache;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.recipe.custom.SummoningRecipe;
import com.benbenlaw.utility.screen.breaker.BlockBreakerScreen;
import com.benbenlaw.utility.screen.collector.FluidCollectorScreen;
import com.benbenlaw.utility.screen.drying.DryingTableScreen;
import com.benbenlaw.utility.screen.generator.FluidGeneratorScreen;
import com.benbenlaw.utility.screen.generator.ResourceGeneratorScreen;
import com.benbenlaw.utility.screen.summoning.SummoningBlockScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIUtilityPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return Utility.identifier("jei_plugin");
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAlias(UtilityItems.FLOATER.toStack(), "angel_block");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(DryingTableRecipeCategory.RECIPE_TYPE, new ItemStack(UtilityBlocks.DRYING_TABLE.get()));
        registration.addCraftingStation(ResourceGeneratorRecipeCategory.RECIPE_TYPE, new ItemStack(UtilityBlocks.RESOURCE_GENERATOR.get()));
        registration.addCraftingStation(FluidGeneratorRecipeCategory.RECIPE_TYPE, new ItemStack(UtilityBlocks.FLUID_GENERATOR.get()));
        registration.addCraftingStation(SummoningRecipeCategory.RECIPE_TYPE, new ItemStack(UtilityBlocks.SUMMONING_BLOCK.get()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DryingTableRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ResourceGeneratorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FluidGeneratorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SummoningRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(DryingTableRecipeCategory.RECIPE_TYPE, ClientRecipeCache.getCachedDryingTableRecipes().stream().toList());
        registration.addRecipes(ResourceGeneratorRecipeCategory.RECIPE_TYPE, ClientRecipeCache.getCachedResourceGeneratorRecipes().stream().toList());
        registration.addRecipes(FluidGeneratorRecipeCategory.RECIPE_TYPE, ClientRecipeCache.getCachedFluidGeneratorRecipes().stream().toList());
        registration.addRecipes(SummoningRecipeCategory.RECIPE_TYPE, ClientRecipeCache.getCachedSummoningRecipes().stream().toList());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(DryingTableScreen.class, 76, 34, 24, 17, DryingTableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(ResourceGeneratorScreen.class, 76, 34, 24, 17, ResourceGeneratorRecipeCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(FluidGeneratorScreen.class, 76, 34, 24, 17, FluidGeneratorRecipeCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(SummoningBlockScreen.class, 76, 25, 24, 17, SummoningRecipeCategory.RECIPE_TYPE);

        registration.addGhostIngredientHandler(BlockBreakerScreen.class, new GhostFilter<>());
        registration.addGhostIngredientHandler(FluidCollectorScreen.class, new GhostFilter<>());

    }
}
