package com.benbenlaw.utility.data.custom;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.recipe.SummoningRecipeInput;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.SummoningRecipe;
import com.benbenlaw.utility.util.BlockTarget;
import com.benbenlaw.utility.util.TemperatureValues;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SummoningRecipeBuilder implements RecipeBuilder {

    protected String group;
    protected SizedIngredient input;
    protected BlockTarget belowBlock;
    protected EntityType<?> summonedEntity;
    protected Optional<CompoundTag> entityData;
    protected Optional<TemperatureValues> temperatureValues;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public SummoningRecipeBuilder(SizedIngredient input, BlockTarget belowBlock, EntityType<?> summonedEntity, Optional<CompoundTag> entityData, Optional<TemperatureValues> temperatureValues) {
        this.input = input;
        this.belowBlock = belowBlock;
        this.summonedEntity = summonedEntity;
        this.entityData = entityData;
        this.temperatureValues = temperatureValues;

    }

    public static SummoningRecipeBuilder summoningRecipe(SizedIngredient input, BlockTarget belowBlock, EntityType<?> summonedEntity, Optional<CompoundTag> entityData, Optional<TemperatureValues> temperatureValues) {
        return new SummoningRecipeBuilder(input, belowBlock, summonedEntity, entityData, temperatureValues);
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
        Identifier entity = EntityType.getKey(summonedEntity);
        return ResourceKey.create(
                Registries.RECIPE,
                Utility.identifier("summoning/" + entity.getPath())
        );
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        save(recipeOutput, ResourceKey.create(Registries.RECIPE, Utility.identifier("summoning/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        SummoningRecipe clocheRecipe = new SummoningRecipe(input, belowBlock, summonedEntity, entityData, temperatureValues);
        recipeOutput.accept(resourceKey, clocheRecipe, builder.build(resourceKey.identifier().withPrefix("recipes/summoning/")));
    }
}
