package com.benbenlaw.utility.data.custom;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class DryingTableRecipeBuilder implements RecipeBuilder {

    protected String group;
    protected SizedIngredient input;
    protected ItemStack output;
    protected DryingTableRecipeType recipeType;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public DryingTableRecipeBuilder(SizedIngredient input, ItemStack output, DryingTableRecipeType recipeType) {
        this.input = input;
        this.output = output;
        this.recipeType = recipeType;
    }

    public static DryingTableRecipeBuilder dryingTable(SizedIngredient input, ItemStack output, DryingTableRecipeType recipeType) {
        return new DryingTableRecipeBuilder(input, output, recipeType);
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return output.getItem();
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        save(recipeOutput, ResourceKey.create(Registries.RECIPE, Utility.rl("drying_table/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        DryingTableRecipe clocheRecipe = new DryingTableRecipe(input, output, recipeType);
        recipeOutput.accept(resourceKey, clocheRecipe, builder.build(resourceKey.location().withPrefix("recipes/drying_table/")));
    }
}
