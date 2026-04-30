package com.benbenlaw.utility.data.custom;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class DryingTableRecipeBuilder implements RecipeBuilder {

    protected String group;
    protected SizedIngredient input;
    protected ItemStackTemplate output;
    protected Optional<FluidStackTemplate> fluid;
    protected Optional<Integer> consumeAmount;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public DryingTableRecipeBuilder(SizedIngredient input, ItemStackTemplate output, Optional<FluidStackTemplate> fluid, Optional<Integer> consumeAmount) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.consumeAmount = consumeAmount;
    }

    public static DryingTableRecipeBuilder dryingTable(SizedIngredient input, ItemStackTemplate output, Optional<FluidStackTemplate> fluid, Optional<Integer> consumeAmount) {
        return new DryingTableRecipeBuilder(input, output, fluid, consumeAmount);
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
    public ResourceKey<Recipe<?>> defaultId() {
        ItemStack stack = output.create();
        return ResourceKey.create(
                Registries.RECIPE,
                Utility.identifier("drying_table/" + stack.getItem().builtInRegistryHolder().key().identifier().getPath())
        );
    }
    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        save(recipeOutput, ResourceKey.create(Registries.RECIPE, Utility.identifier("drying_table/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        DryingTableRecipe dryingTableRecipe = new DryingTableRecipe(input, output, fluid, consumeAmount);
        recipeOutput.accept(resourceKey, dryingTableRecipe, builder.build(resourceKey.identifier().withPrefix("recipes/drying_table/")));
    }
}
