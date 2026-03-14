package com.benbenlaw.utility.data.custom;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.recipe.custom.FluidGeneratorRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class FluidGeneratorRecipeBuilder implements RecipeBuilder {

    protected String group;
    protected String input;
    protected FluidStack output;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public FluidGeneratorRecipeBuilder(String input, FluidStack output) {
        this.input = input;
        this.output = output;
    }

    public static FluidGeneratorRecipeBuilder fluidGenerator(String input, FluidStack output) {
        return new FluidGeneratorRecipeBuilder(input, output);
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
        FluidStack stack = output.copy();
        return ResourceKey.create(
                Registries.RECIPE,
                Utility.identifier("fluid_generator/" + stack.getFluid().builtInRegistryHolder().key().identifier().getPath())
        );
    }
    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        save(recipeOutput, ResourceKey.create(Registries.RECIPE, Utility.identifier("fluid_generator/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        FluidGeneratorRecipe clocheRecipe = new FluidGeneratorRecipe(input, output);
        recipeOutput.accept(resourceKey, clocheRecipe, builder.build(resourceKey.identifier().withPrefix("recipes/fluid_generator/")));
    }
}
