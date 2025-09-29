package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.block.custom.ResourceGeneratorBlock;
import com.benbenlaw.utility.event.ClientRecipeCache;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.screen.drying.DryingTableScreen;
import com.benbenlaw.utility.screen.generator.ResourceGeneratorScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIUtilityPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Utility.rl("jei_plugin");
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAlias(UtilityItems.FLOATER.toStack(), "angel_block");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(DryingTableRecipeCategory.RECIPE_TYPE, new ItemStack(UtilityBlocks.DRYING_TABLE.get()));
        registration.addCraftingStation(ResourceGeneratorRecipeCategory.RECIPE_TYPE, new ItemStack(UtilityBlocks.RESOURCE_GENERATOR.get()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DryingTableRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ResourceGeneratorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(DryingTableRecipeCategory.RECIPE_TYPE, ClientRecipeCache.getCachedDryingTableRecipes().stream().toList());
        registration.addRecipes(ResourceGeneratorRecipeCategory.RECIPE_TYPE, ClientRecipeCache.getCachedResourceGeneratorRecipes().stream().toList());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(DryingTableScreen.class, 76, 34, 24, 17, DryingTableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(ResourceGeneratorScreen.class, 76, 34, 24, 17, ResourceGeneratorRecipeCategory.RECIPE_TYPE);
    }
}
