package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.data.custom.DryingTableRecipeBuilder;
import com.benbenlaw.utility.data.custom.ResourceGeneratorRecipeBuilder;
import com.benbenlaw.utility.integration.jei.ResourceGeneratorRecipeCategory;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
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
                .save(output, String.valueOf(Utility.rl("coal")));

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
                .save(output, String.valueOf(Utility.rl("charcoal")));

        //Mini Charcoal
        shapeless(RecipeCategory.MISC, UtilityItems.MINI_CHARCOAL.get(), 8)
                .requires(Items.CHARCOAL)
                .group("utility")
                .unlockedBy("has_item", has(Items.CHARCOAL))
                .save(output);

        //Chest
        shaped(RecipeCategory.MISC, Items.CHEST)
                .pattern("AAA")
                .pattern("A A")
                .pattern("AAA")
                .define('A', ItemTags.LOGS)
                .group("utility")
                .unlockedBy("has_item", has(Items.CHEST))
                .save(output, String.valueOf(Utility.rl("chests")));

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
                .save(output, String.valueOf(Utility.rl("iron_horse_armor")));

        //Gold Horse Armor
        shaped(RecipeCategory.MISC, Items.GOLDEN_HORSE_ARMOR)
                .pattern("AAA")
                .pattern("ABA")
                .define('A', Items.GOLD_INGOT)
                .define('B', Items.LEATHER_HORSE_ARMOR)
                .group("utility")
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(output, String.valueOf(Utility.rl("gold_horse_armor")));

        //Diamond Horse Armor
        shaped(RecipeCategory.MISC, Items.DIAMOND_HORSE_ARMOR)
                .pattern("AAA")
                .pattern("ABA")
                .define('A', Items.DIAMOND)
                .define('B', Items.LEATHER_HORSE_ARMOR)
                .group("utility")
                .unlockedBy("has_item", has(Items.DIAMOND))
                .save(output, String.valueOf(Utility.rl("diamond_horse_armor")));

        //Drying Table
        shaped(RecipeCategory.DECORATIONS, UtilityBlocks.DRYING_TABLE.get())
                .pattern("ABA")
                .pattern("ABA")
                .pattern("A A")
                .define('A', ItemTags.LOGS)
                .define('B', Tags.Items.STRINGS)
                .group("utility")
                .unlockedBy("has_item", has(Items.CRAFTING_TABLE))
                .save(output);

        //Ender Pearl
        shapeless(RecipeCategory.MISC, Items.ENDER_PEARL)
                .requires(UtilityItems.ENDER_PEARL_FRAGMENT, 8)
                .unlockedBy("has_item", has(UtilityItems.ENDER_PEARL_FRAGMENT))
                .group("utility")
                .save(output, String.valueOf(Utility.rl("ender_pearl")));

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
                .save(output, String.valueOf(Utility.rl("green_wool")));

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
                .save(output, String.valueOf(Utility.rl("name_tag")));

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
                .save(output, String.valueOf(Utility.rl("sticks")));

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
                new ItemStack(Items.DEAD_BUSH), DryingTableRecipeType.DRYING).save(output, "drying/dead_bush");

        //Cracked Stone Bricks
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.STONE_BRICKS), 1),
                new ItemStack(Items.CRACKED_STONE_BRICKS), DryingTableRecipeType.DRYING).save(output, "drying/cracked_stone_bricks");

        //Paper
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(UtilityItems.SOAKED_PAPER), 1),
                new ItemStack(Items.PAPER), DryingTableRecipeType.DRYING).save(output, "drying/paper");

        //Soaked Paper
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.PAPER), 1),
                new ItemStack(UtilityItems.SOAKED_PAPER.get()), DryingTableRecipeType.SOAKING).save(output, "soaking/soaked_paper");

        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(UtilityItems.LOG_SHEET), 1),
                new ItemStack(UtilityItems.SOAKED_PAPER.get()), DryingTableRecipeType.SOAKING).save(output, "soaking/soaked_paper_from_log_sheet");

        //Sponge
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.WET_SPONGE), 1),
                new ItemStack(Items.SPONGE), DryingTableRecipeType.DRYING).save(output, "drying/sponge");

        //Dry Sponge
        DryingTableRecipeBuilder.dryingTable(new SizedIngredient(Ingredient.of(Items.SPONGE), 1),
                new ItemStack(Items.WET_SPONGE), DryingTableRecipeType.SOAKING).save(output, "soaking/wet_sponge");

        //Resource Generator
        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.COBBLESTONE.getDefaultInstance(), Items.COBBLESTONE.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "cobblestone");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.STONE.getDefaultInstance(), Items.STONE.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "stone");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.ANDESITE.getDefaultInstance(), Items.ANDESITE.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "andesite");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.DIORITE.getDefaultInstance(), Items.DIORITE.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "diorite");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.GRANITE.getDefaultInstance(), Items.GRANITE.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "granite");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.DEEPSLATE.getDefaultInstance(), Items.DEEPSLATE.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "deepslate");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.COBBLED_DEEPSLATE.getDefaultInstance(), Items.COBBLED_DEEPSLATE.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "cobbled_deepslate");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.TUFF.getDefaultInstance(), Items.TUFF.getDefaultInstance(),
                new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, false)
                .save(output, "tuff");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.OBSIDIAN.getDefaultInstance(), Items.OBSIDIAN.getDefaultInstance(),
                        new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000), false, true)
                .save(output, "obsidian");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.BASALT.getDefaultInstance(), Items.BASALT.getDefaultInstance(),
                        new FluidStack(Fluids.WATER, 25), new FluidStack(Fluids.LAVA, 25), true, true)
                .save(output, "basalt");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.BLACKSTONE.getDefaultInstance(), Items.BLACKSTONE.getDefaultInstance(),
                        new FluidStack(Fluids.WATER, 25), new FluidStack(Fluids.LAVA, 25), true, true)
                .save(output, "blackstone");

        ResourceGeneratorRecipeBuilder.resourceGenerator(Items.NETHERRACK.getDefaultInstance(), Items.NETHERRACK.getDefaultInstance(),
                        new FluidStack(Fluids.WATER, 25), new FluidStack(Fluids.LAVA, 25), true, true)
                .save(output, "netherrack");



    }

    protected void oreSmelting(List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_) {
        this.oreCooking(RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, p_250172_, p_250588_, p_251868_, p_250789_, p_252144_, p_251687_, "_from_smelting");
    }

    protected <T extends AbstractCookingRecipe> void oreCooking(RecipeSerializer<T> p_251817_, AbstractCookingRecipe.Factory<T> p_312707_, List<ItemLike> p_249619_, RecipeCategory p_251154_, ItemLike p_250066_, float p_251871_, int p_251316_, String p_251450_, String p_249236_) {
        for(ItemLike itemlike : p_249619_) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), p_251154_, p_250066_, p_251871_, p_251316_, p_251817_, p_312707_).group(p_251450_).unlockedBy(getHasName(itemlike), this.has(itemlike)).save(this.output, String.valueOf(Utility.rl(getItemName(p_250066_) + p_249236_ + "_" + getItemName(itemlike))));
        }

    }

}
