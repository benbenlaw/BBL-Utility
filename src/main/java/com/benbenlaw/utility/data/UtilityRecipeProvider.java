package com.benbenlaw.utility.data;

import com.benbenlaw.core.util.ColorUtils;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.data.custom.DryingTableRecipeBuilder;
import com.benbenlaw.utility.data.custom.FluidGeneratorRecipeBuilder;
import com.benbenlaw.utility.data.custom.ResourceGeneratorRecipeBuilder;
import com.benbenlaw.utility.data.custom.SummoningRecipeBuilder;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.util.BlockTarget;
import com.benbenlaw.utility.util.TemperatureValues;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UtilityRecipeProvider extends RecipeProvider {

    public static final ImmutableList<ItemLike> ENDER_SMELTABLES = ImmutableList.of(UtilityBlocks.ENDER_ORE.get(), UtilityBlocks.DEEPSLATE_ENDER_ORE.get());


    public UtilityRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new UtilityRecipeProvider(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return Utility.MOD_ID + " Recipes";
        }
    }

    @Override
    protected void buildRecipes() {

        //Reset
        shapeless(RecipeCategory.MISC, UtilityBlocks.DRYING_TABLE)
                .requires(UtilityBlocks.DRYING_TABLE)
                .unlockedBy("has_modifier", has(UtilityBlocks.DRYING_TABLE))
                .save(output, "utility:reset/drying_table");

        shapeless(RecipeCategory.MISC, UtilityBlocks.RESOURCE_GENERATOR)
                .requires(UtilityBlocks.RESOURCE_GENERATOR)
                .unlockedBy("has_modifier", has(UtilityBlocks.RESOURCE_GENERATOR))
                .save(output, "utility:reset/resource_generator");

        shapeless(RecipeCategory.MISC, UtilityBlocks.FLUID_GENERATOR)
                .requires(UtilityBlocks.FLUID_GENERATOR)
                .unlockedBy("has_modifier", has(UtilityBlocks.FLUID_GENERATOR))
                .save(output, "utility:reset/fluid_generator");

        shapeless(RecipeCategory.MISC, UtilityBlocks.FLUID_PLACER)
                .requires(UtilityBlocks.FLUID_PLACER)
                .unlockedBy("has_modifier", has(UtilityBlocks.FLUID_PLACER))
                .save(output, "utility:reset/fluid_placer");

        shapeless(RecipeCategory.MISC, UtilityBlocks.FLUID_COLLECTOR)
                .requires(UtilityBlocks.FLUID_COLLECTOR)
                .unlockedBy("has_modifier", has(UtilityBlocks.FLUID_COLLECTOR))
                .save(output, "utility:reset/fluid_collector");

        //Compactor
        shaped(RecipeCategory.MISC, UtilityBlocks.COMPACTOR.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
                .define('D', Items.PISTON)
                .group("utility")
                .unlockedBy("has_item", has(Items.PISTON))
                .save(output);

        //Summoning Block
        shaped(RecipeCategory.MISC, UtilityBlocks.SUMMONING_BLOCK.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.ENDER_EYE)
                .define('D', Items.GLOWSTONE)
                .group("utility")
                .unlockedBy("has_item", has(Items.GLOWSTONE))
                .save(output);

        //Item Collector
        shaped(RecipeCategory.MISC, UtilityBlocks.ITEM_COLLECTOR.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.ENDER_EYE)
                .define('D', Items.HOPPER)
                .group("utility")
                .unlockedBy("has_item", has(Items.ENDER_EYE))
                .save(output);

        //Drying Table
        shaped(RecipeCategory.MISC, UtilityBlocks.DRYING_TABLE.get())
                .pattern("ABA")
                .pattern("CCC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.NUGGETS_IRON)
                .define('C', Tags.Items.STRINGS)
                .group("utility")
                .unlockedBy("has_item", has(Tags.Items.INGOTS_IRON))
                .save(output);

        //Block Placer
        shaped(RecipeCategory.MISC, UtilityBlocks.BLOCK_PLACER.get())
                .pattern("ABA")
                .pattern("C C")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Tags.Items.STONES)
                .group("utility")
                .unlockedBy("has_item", has(Items.DISPENSER))
                .save(output);

        //Block Breaker
        shaped(RecipeCategory.MISC, UtilityBlocks.BLOCK_BREAKER.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Tags.Items.RODS_WOODEN)
                .define('D', Tags.Items.TOOLS)
                .group("utility")
                .unlockedBy("has_item", has(Items.DISPENSER))
                .save(output);

        //Resource Generator
        shaped(RecipeCategory.MISC, UtilityBlocks.RESOURCE_GENERATOR.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.BUCKET)
                .define('D', Tags.Items.CHESTS_WOODEN)
                .group("utility")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(output);

        //Fluid Generator
        shaped(RecipeCategory.MISC, UtilityBlocks.FLUID_GENERATOR.get())
                .pattern("ABA")
                .pattern("CCC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.BUCKET)
                .group("utility")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(output);

        //Fluid Placer
        shaped(RecipeCategory.MISC, UtilityBlocks.FLUID_PLACER.get())
                .pattern("ABA")
                .pattern("C C")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.BUCKET)
                .group("utility")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(output);

        //Fluid Collector
        shaped(RecipeCategory.MISC, UtilityBlocks.FLUID_COLLECTOR.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.BUCKET)
                .define('D', Tags.Items.GLASS_BLOCKS)
                .group("utility")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(output);

        //Item Repairer
        shaped(RecipeCategory.MISC, UtilityBlocks.ITEM_REPAIRER.get())
                .pattern("ABA")
                .pattern("CCC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.ANVIL)
                .group("utility")
                .unlockedBy("has_item", has(Items.ANVIL))
                .save(output);

        //Redstone Clock
        shaped(RecipeCategory.MISC, UtilityBlocks.REDSTONE_CLOCK.get())
                .pattern("ABA")
                .pattern("CCC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Items.REDSTONE)
                .group("utility")
                .unlockedBy("has_item", has(Items.REDSTONE))
                .save(output);

        //Animal Net
        shaped(RecipeCategory.MISC, UtilityItems.ANIMAL_NET.get())
                .pattern("ABA")
                .pattern("B B")
                .pattern("ABA")
                .define('A', Tags.Items.RODS_WOODEN)
                .define('B', Items.LEATHER)
                .group("utility")
                .unlockedBy("has_item", has(Tags.Items.RODS_WOODEN))
                .save(output);

        //Coal
        shapeless(RecipeCategory.MISC, Items.COAL)
                .requires(UtilityItems.MINI_COAL, 8)
                .unlockedBy("has_item", has(UtilityItems.MINI_COAL))
                .group("utility")
                .save(output, String.valueOf(Utility.identifier("coal")));

        //Mini Coal
        shapeless(RecipeCategory.MISC, UtilityItems.MINI_COAL.get(), 8)
                .requires(Items.COAL)
                .unlockedBy("has_item", has(Items.COAL))
                .save(output);

        //Charcoal
        shapeless(RecipeCategory.MISC, Items.CHARCOAL)
                .requires(UtilityItems.MINI_CHARCOAL, 8)
                .unlockedBy("has_item", has(UtilityItems.MINI_CHARCOAL))
                .group("utility")
                .save(output, String.valueOf(Utility.identifier("charcoal")));

        //Mini Charcoal
        shapeless(RecipeCategory.MISC, UtilityItems.MINI_CHARCOAL.get(), 8)
                .requires(Items.CHARCOAL)
                .group("utility")
                .unlockedBy("has_item", has(Items.CHARCOAL))
                .save(output);

        //Chest
        shaped(RecipeCategory.MISC, Items.CHEST, 4)
                .pattern("AAA")
                .pattern("A A")
                .pattern("AAA")
                .define('A', ItemTags.LOGS)
                .group("utility")
                .unlockedBy("has_item", has(Items.CHEST))
                .save(output, String.valueOf(Utility.identifier("chests")));

        //Crook
        shaped(RecipeCategory.TOOLS, UtilityItems.CROOK.get())
                .pattern(" AA")
                .pattern(" A ")
                .pattern(" A ")
                .define('A', Tags.Items.RODS_WOODEN)
                .group("utility")
                .unlockedBy("has_item", has(Tags.Items.RODS_WOODEN))
                .save(output);

        //Iron Horse Armor
        shaped(RecipeCategory.MISC, Items.IRON_HORSE_ARMOR)
                .pattern("AAA")
                .pattern("ABA")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.LEATHER_HORSE_ARMOR)
                .group("utility")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(output, String.valueOf(Utility.identifier("iron_horse_armor")));

        //Gold Horse Armor
        shaped(RecipeCategory.MISC, Items.GOLDEN_HORSE_ARMOR)
                .pattern("AAA")
                .pattern("ABA")
                .define('A', Items.GOLD_INGOT)
                .define('B', Items.LEATHER_HORSE_ARMOR)
                .group("utility")
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(output, String.valueOf(Utility.identifier("gold_horse_armor")));

        //Diamond Horse Armor
        shaped(RecipeCategory.MISC, Items.DIAMOND_HORSE_ARMOR)
                .pattern("AAA")
                .pattern("ABA")
                .define('A', Items.DIAMOND)
                .define('B', Items.LEATHER_HORSE_ARMOR)
                .group("utility")
                .unlockedBy("has_item", has(Items.DIAMOND))
                .save(output, String.valueOf(Utility.identifier("diamond_horse_armor")));

        //Ender Pearl
        shapeless(RecipeCategory.MISC, Items.ENDER_PEARL)
                .requires(UtilityItems.ENDER_PEARL_FRAGMENT, 8)
                .unlockedBy("has_item", has(UtilityItems.ENDER_PEARL_FRAGMENT))
                .group("utility")
                .save(output, String.valueOf(Utility.identifier("ender_pearl")));

        //Ender Pearl Fragment
        shapeless(RecipeCategory.MISC, UtilityItems.ENDER_PEARL_FRAGMENT.get(), 8)
                .requires(Items.ENDER_PEARL)
                .unlockedBy("has_item", has(Items.ENDER_PEARL))
                .save(output);

        //Floater
        shaped(RecipeCategory.MISC, UtilityItems.FLOATER.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Tags.Items.FEATHERS)
                .define('B', ItemTags.WOOL)
                .group("utility")
                .unlockedBy("has_item", has(Tags.Items.FEATHERS))
                .save(output);

        //Green Wool
        shaped(RecipeCategory.MISC, Items.GREEN_WOOL)
                .pattern("AA")
                .pattern("AA")
                .define('A', UtilityItems.LEAFY_STRING)
                .group("utility")
                .unlockedBy("has_item", has(UtilityItems.LEAFY_STRING))
                .save(output, String.valueOf(Utility.identifier("green_wool")));

        //Leafy String
        shaped(RecipeCategory.MISC, UtilityItems.LEAFY_STRING.get())
                .pattern("AAA")
                .define('A', ItemTags.LEAVES)
                .group("utility")
                .unlockedBy("has_item", has(ItemTags.LEAVES))
                .save(output);

        //Log Sheet
        shaped(RecipeCategory.MISC, UtilityItems.LOG_SHEET.get(), 6)
                .pattern(" A ")
                .pattern("A A")
                .pattern(" A ")
                .define('A', ItemTags.LOGS)
                .group("utility")
                .unlockedBy("has_item", has(ItemTags.LOGS))
                .save(output);

        //Name Tag
        shaped(RecipeCategory.MISC, Items.NAME_TAG)
                .pattern("  A")
                .pattern(" B ")
                .pattern("B  ")
                .define('A', Tags.Items.STRINGS)
                .define('B', Items.PAPER)
                .group("utility")
                .unlockedBy("has_item", has(Items.PAPER))
                .save(output, String.valueOf(Utility.identifier("name_tag")));

        //Sapling Grower
        shaped(RecipeCategory.MISC, UtilityItems.SAPLING_GROWER.get())
                .pattern("  A")
                .pattern(" B ")
                .pattern("B  ")
                .define('A', Items.BONE_BLOCK)
                .define('B', Tags.Items.RODS_WOODEN)
                .group("utility")
                .unlockedBy("has_item", has(Items.BONE_MEAL))
                .save(output);

        //Sticks
        shaped(RecipeCategory.MISC, Items.STICK, 16)
                .pattern("A")
                .pattern("A")
                .define('A', ItemTags.LOGS)
                .group("utility")
                .unlockedBy("has_item", has(UtilityItems.LOG_SHEET.get()))
                .save(output, String.valueOf(Utility.identifier("sticks")));

        //Wooden Shears
        shaped(RecipeCategory.TOOLS, UtilityItems.WOODEN_SHEARS.get())
                .pattern(" A")
                .pattern("A ")
                .define('A', ItemTags.PLANKS)
                .group("utility")
                .unlockedBy("has_item", has(Tags.Items.RODS_WOODEN))
                .save(output);

        //Ender Pearl Fragment
        oreSmelting(ENDER_SMELTABLES, RecipeCategory.MISC, UtilityItems.ENDER_PEARL_FRAGMENT, 0.7f, 200, "ender");

        //Drying Table Recipes

        //Dead Bush
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(this.tag(ItemTags.SAPLINGS).getValues()), 1),
                new ItemStackTemplate(Items.DEAD_BUSH), Optional.empty(), Optional.empty()).save(output, "drying/dead_bush");

        //Cracked Stone Bricks
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.STONE_BRICKS), 1),
                new ItemStackTemplate(Items.CRACKED_STONE_BRICKS), Optional.empty(), Optional.empty()).save(output, "drying/cracked_stone_bricks");

        //Paper
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(UtilityItems.SOAKED_PAPER), 1),
                new ItemStackTemplate(Items.PAPER), Optional.empty(), Optional.empty()).save(output, "drying/paper");

        //Soaked Paper
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.PAPER), 1),
                new ItemStackTemplate(UtilityItems.SOAKED_PAPER.get()), Optional.of(new FluidStackTemplate(Fluids.WATER, 1000)), Optional.empty()).save(output, "soaking/soaked_paper");

        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(UtilityItems.LOG_SHEET), 1),
                new ItemStackTemplate(UtilityItems.SOAKED_PAPER.get()), Optional.of(new FluidStackTemplate(Fluids.WATER, 1000)), Optional.of(100)).save(output, "soaking/soaked_paper_from_log_sheet");

        //Sponge
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.WET_SPONGE), 1),
                new ItemStackTemplate(Items.SPONGE), Optional.empty(), Optional.empty()).save(output, "drying/sponge");

        //Dry Sponge
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.SPONGE), 1),
                new ItemStackTemplate(Items.WET_SPONGE), Optional.of(new FluidStackTemplate(Fluids.WATER, 1000)), Optional.of(1000)).save(output, "soaking/wet_sponge");

        //Resource Generator
        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.COBBLESTONE), new ItemStackTemplate(Items.COBBLESTONE),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "cobblestone");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.STONE), new ItemStackTemplate(Items.STONE),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "stone");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.ANDESITE), new ItemStackTemplate(Items.ANDESITE),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "andesite");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.DIORITE), new ItemStackTemplate(Items.DIORITE),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "diorite");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.GRANITE), new ItemStackTemplate(Items.GRANITE),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "granite");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.DEEPSLATE), new ItemStackTemplate(Items.DEEPSLATE),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "deepslate");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.COBBLED_DEEPSLATE), new ItemStackTemplate(Items.COBBLED_DEEPSLATE),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "cobbled_deepslate");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.TUFF), new ItemStackTemplate(Items.TUFF),
                new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, false)
                .save(output, "tuff");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.OBSIDIAN), new ItemStackTemplate(Items.OBSIDIAN),
                        new FluidStackTemplate(Fluids.WATER, 1000), new FluidStackTemplate(Fluids.LAVA, 1000), false, true)
                .save(output, "obsidian");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.BASALT), new ItemStackTemplate(Items.BASALT),
                        new FluidStackTemplate(Fluids.WATER, 25), new FluidStackTemplate(Fluids.LAVA, 25), true, true)
                .save(output, "basalt");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.BLACKSTONE), new ItemStackTemplate(Items.BLACKSTONE),
                        new FluidStackTemplate(Fluids.WATER, 25), new FluidStackTemplate(Fluids.LAVA, 25), true, true)
                .save(output, "blackstone");

        ResourceGeneratorRecipeBuilder.resourceGenerator(new ItemStackTemplate(Items.NETHERRACK), new ItemStackTemplate(Items.NETHERRACK),
                        new FluidStackTemplate(Fluids.WATER, 25), new FluidStackTemplate(Fluids.LAVA, 25), true, true)
                .save(output, "netherrack");

        //Fluid Generator
        FluidGeneratorRecipeBuilder.fluidGenerator("water", new FluidStackTemplate(Fluids.WATER, 100)).save(output, "water");
        FluidGeneratorRecipeBuilder.fluidGenerator("lava", new FluidStackTemplate(Fluids.LAVA, 50)).save(output, "lava");

        //Summoning
        CompoundTag warmEntityData = new CompoundTag();
        warmEntityData.putString("variant", TemperatureVariants.WARM.toString());

        CompoundTag coldEntityData = new CompoundTag();
        coldEntityData.putString("variant", TemperatureVariants.COLD.toString());

        CompoundTag temperateEntityData = new CompoundTag();
        temperateEntityData.putString("variant", TemperatureVariants.TEMPERATE.toString());

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.LEATHER), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.COW, Optional.of(warmEntityData), Optional.of(TemperatureValues.WARM)).save(output, "cows/warm");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.LEATHER), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.COW, Optional.of(coldEntityData), Optional.of(TemperatureValues.COLD)).save(output, "cows/cold");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.LEATHER), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.COW, Optional.of(temperateEntityData), Optional.of(TemperatureValues.TEMPERATE)).save(output, "cows/temperate");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.CHICKEN), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.CHICKEN, Optional.of(warmEntityData), Optional.of(TemperatureValues.WARM)).save(output, "chicken/warm");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.CHICKEN), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.CHICKEN, Optional.of(coldEntityData), Optional.of(TemperatureValues.COLD)).save(output, "chicken/cold");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.CHICKEN), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.CHICKEN, Optional.of(temperateEntityData), Optional.of(TemperatureValues.TEMPERATE)).save(output, "chicken/temperate");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.PORKCHOP), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.PIG, Optional.of(temperateEntityData), Optional.of(TemperatureValues.TEMPERATE)).save(output, "pig/temperate");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.PORKCHOP), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.PIG, Optional.of(warmEntityData), Optional.of(TemperatureValues.WARM)).save(output, "pig/warm");

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.PORKCHOP), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.PIG, Optional.of(coldEntityData), Optional.of(TemperatureValues.COLD)).save(output, "pig/cold");

        //Sheep
        for (ColorUtils color : ColorUtils.values()) {
            CompoundTag colorData = new CompoundTag();
            colorData.putInt("Color", color.getDyeColor().getId());
            Ingredient woolIngredient = Ingredient.of(BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color.toString().toLowerCase() + "_wool")));
            SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(woolIngredient, 1), new BlockTarget.Tag(BlockTags.DIRT),
                    EntityType.SHEEP, Optional.of(colorData), Optional.empty()).save(output, "sheep/" + color.toString().toLowerCase());
        }

        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(tag(ItemTags.WOOL_CARPETS), 1), new BlockTarget.Tag(BlockTags.SAND),
                EntityType.CAMEL, Optional.empty(), Optional.empty()).save(output, "camel");

        //Donkey
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.BREAD), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.DONKEY, Optional.empty(), Optional.empty()).save(output, "donkey");

        //Horse
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.HAY_BLOCK), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.HORSE, Optional.empty(), Optional.empty()).save(output, "horse");

        //Mule
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.HAY_BLOCK), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.MULE, Optional.empty(), Optional.empty()).save(output, "mule");

        //Cat
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(tag(ItemTags.FISHES), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.CAT, Optional.empty(), Optional.empty()).save(output, "cat");

        //Parrot
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(tag(Tags.Items.SEEDS), 1), new BlockTarget.Tag(BlockTags.LEAVES),
                EntityType.PARROT, Optional.empty(), Optional.empty()).save(output, "parrot");

        //Wolf
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.BONE), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.WOLF, Optional.empty(), Optional.empty()).save(output, "wolf");

        //Armadillo
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.ARMADILLO_SCUTE), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.ARMADILLO, Optional.empty(), Optional.empty()).save(output, "armadillo");

        //Bat
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.FIREFLY_BUSH), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.BAT, Optional.empty(), Optional.empty()).save(output, "bat");

        //Bee
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(tag(Tags.Items.FLOWERS), 1), new BlockTarget.Single(Blocks.BEEHIVE.defaultBlockState()),
                EntityType.BEE, Optional.empty(), Optional.empty()).save(output, "bee");

        //Fox
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.SWEET_BERRIES), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.FOX, Optional.empty(), Optional.empty()).save(output, "fox");

        //Goat
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.WHEAT), 1), new BlockTarget.Tag(Tags.Blocks.STONES),
                EntityType.GOAT, Optional.empty(), Optional.empty()).save(output, "goat");

        //Llama
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.HAY_BLOCK), 1), new BlockTarget.Tag(Tags.Blocks.STONES),
                EntityType.LLAMA, Optional.empty(), Optional.empty()).save(output, "llama");

        //Ocelot
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(tag(ItemTags.FISHES), 1), new BlockTarget.Tag(BlockTags.JUNGLE_LOGS),
                EntityType.OCELOT, Optional.empty(), Optional.empty()).save(output, "ocelot");

        //Panda
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.BAMBOO), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.PANDA, Optional.empty(), Optional.empty()).save(output, "panda");

        //Polar Bear
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.SALMON), 1), new BlockTarget.Single(Blocks.ICE.defaultBlockState()),
                EntityType.POLAR_BEAR, Optional.empty(), Optional.empty()).save(output, "polar_bear");

        //Rabbit
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.CARROT), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.RABBIT, Optional.empty(), Optional.empty()).save(output, "rabbit");

        //Axolotl
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.TROPICAL_FISH), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.AXOLOTL, Optional.empty(), Optional.empty()).save(output, "axolotl");

        //Cod
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.BREAD), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.COD, Optional.empty(), Optional.empty()).save(output, "cod");

        //Dolphin
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.COD), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.DOLPHIN, Optional.empty(), Optional.empty()).save(output, "dolphin");

        //Frog
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.SLIME_BALL), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.FROG, Optional.empty(), Optional.empty()).save(output, "frog");

        //Glow Squid
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.GLOW_INK_SAC), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.GLOW_SQUID, Optional.empty(), Optional.empty()).save(output, "glow_squid");

        //Nautilus
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.PRISMARINE_SHARD), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.NAUTILUS, Optional.empty(), Optional.empty()).save(output, "nautilus");

        //Pufferfish
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.SEAGRASS), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.PUFFERFISH, Optional.empty(), Optional.empty()).save(output, "pufferfish");

        //Salmon
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.SEAGRASS), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.SALMON, Optional.empty(), Optional.empty()).save(output, "salmon");

        //Squid
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.INK_SAC), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.SQUID, Optional.empty(), Optional.empty()).save(output, "squid");

        //Tadpole
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.SLIME_BALL), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.TADPOLE, Optional.empty(), Optional.empty()).save(output, "tadpole");

        //Tropical Fish
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.SEAGRASS), 1), new BlockTarget.Single(Blocks.WATER.defaultBlockState()),
                EntityType.TROPICAL_FISH, Optional.empty(), Optional.empty()).save(output, "tropical_fish");

        //Turtle
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.TURTLE_SCUTE), 1), new BlockTarget.Single(Blocks.SAND.defaultBlockState()),
                EntityType.TURTLE, Optional.empty(), Optional.empty()).save(output, "turtle");

        //Allay
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.DIAMOND_BLOCK), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.ALLAY, Optional.empty(), Optional.empty()).save(output, "allay");

        //Mooshroom
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.MUSHROOM_STEW), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.MOOSHROOM, Optional.empty(), Optional.empty()).save(output, "mooshroom");

        //Sniffer
        SummoningRecipeBuilder.summoningRecipe(new SizedIngredient(Ingredient.of(Items.PITCHER_POD), 1), new BlockTarget.Tag(BlockTags.DIRT),
                EntityType.SNIFFER, Optional.empty(), Optional.empty()).save(output, "sniffer");


    }

    protected void oreSmelting(List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_) {
        this.oreCooking(SmeltingRecipe::new, p_250172_, p_250588_, CookingBookCategory.BLOCKS, p_251868_, p_250789_, p_252144_, "smelting/" + p_251687_, "_from");
    }

    protected <T extends AbstractCookingRecipe> void oreCooking(AbstractCookingRecipe.Factory<T> factory, List<ItemLike> smeltables, RecipeCategory craftingCategory, CookingBookCategory cookingCategory, ItemLike result, float experience, int cookingTime, String group, String fromDesc) {
        for(ItemLike item : smeltables) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(item), craftingCategory, cookingCategory, result, experience, cookingTime, factory).group(group).unlockedBy(getHasName(item), this.has(item)).save(this.output, getItemName(result) + fromDesc + "_" + getItemName(item));
        }

    }

}
