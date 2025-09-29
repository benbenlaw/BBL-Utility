package com.benbenlaw.utility.data.custom;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.integration.jei.ResourceGeneratorRecipeCategory;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceGeneratorRecipeBuilder implements RecipeBuilder {

    protected String group;
    protected ItemStack input;
    protected ItemStack output;
    protected FluidStack leftFluid;
    protected FluidStack rightFluid;
    protected boolean consumeLeft;
    protected boolean consumeRight;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public ResourceGeneratorRecipeBuilder(ItemStack input, ItemStack output, FluidStack leftFluid, FluidStack rightFluid, boolean consumeLeft, boolean consumeRight) {
        this.input = input;
        this.output = output;
        this.leftFluid = leftFluid;
        this.rightFluid = rightFluid;
        this.consumeLeft = consumeLeft;
        this.consumeRight = consumeRight;
    }

    public static ResourceGeneratorRecipeBuilder resourceGenerator(ItemStack input, ItemStack output, FluidStack leftFluid, FluidStack rightFluid, boolean consumeLeft, boolean consumeRight) {
        return new ResourceGeneratorRecipeBuilder(input, output, leftFluid, rightFluid, consumeLeft, consumeRight);
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
        save(recipeOutput, ResourceKey.create(Registries.RECIPE, Utility.rl("resource_generator/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        ResourceGeneratorRecipe clocheRecipe = new ResourceGeneratorRecipe(input, output, leftFluid, rightFluid, consumeLeft, consumeRight);
        recipeOutput.accept(resourceKey, clocheRecipe, builder.build(resourceKey.location().withPrefix("recipes/resource_generator/")));
    }
}
