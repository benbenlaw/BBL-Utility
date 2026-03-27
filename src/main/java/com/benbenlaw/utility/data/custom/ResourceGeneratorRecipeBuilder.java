package com.benbenlaw.utility.data.custom;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
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
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceGeneratorRecipeBuilder implements RecipeBuilder {

    protected String group;
    protected ItemStackTemplate input;
    protected ItemStackTemplate output;
    protected FluidStackTemplate leftFluid;
    protected FluidStackTemplate rightFluid;
    protected boolean consumeLeft;
    protected boolean consumeRight;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public ResourceGeneratorRecipeBuilder(ItemStackTemplate input, ItemStackTemplate output, FluidStackTemplate leftFluid, FluidStackTemplate rightFluid, boolean consumeLeft, boolean consumeRight) {
        this.input = input;
        this.output = output;
        this.leftFluid = leftFluid;
        this.rightFluid = rightFluid;
        this.consumeLeft = consumeLeft;
        this.consumeRight = consumeRight;
    }

    public static ResourceGeneratorRecipeBuilder resourceGenerator(ItemStackTemplate input, ItemStackTemplate output, FluidStackTemplate leftFluid, FluidStackTemplate rightFluid, boolean consumeLeft, boolean consumeRight) {
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
    public ResourceKey<Recipe<?>> defaultId() {
        ItemStack stack = output.create();
        return ResourceKey.create(
                Registries.RECIPE,
                Utility.identifier("resource_generator/" + stack.getItem().builtInRegistryHolder().key().identifier().getPath())
        );
    }
    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        save(recipeOutput, ResourceKey.create(Registries.RECIPE, Utility.identifier("resource_generator/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        ResourceGeneratorRecipe clocheRecipe = new ResourceGeneratorRecipe(input, output, leftFluid, rightFluid, consumeLeft, consumeRight);
        recipeOutput.accept(resourceKey, clocheRecipe, builder.build(resourceKey.identifier().withPrefix("recipes/resource_generator/")));
    }
}
