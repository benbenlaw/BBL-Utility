package com.benbenlaw.utility.event;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.event.client.ClientRecipeCache;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.*;
import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.*;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class UtilityEvents {


    @SubscribeEvent
    public static void registerCapabilities(PlayerTickEvent.Post event) {


    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.BLOCK_PLACER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.BLOCK_BREAKER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.DRYING_TABLE_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.DRYING_TABLE_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.RESOURCE_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.RESOURCE_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.ITEM_REPAIRER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.FLUID_COLLECTOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidHandler());

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.FLUID_PLACER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.FLUID_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.FLUID_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.ITEM_COLLECTOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.COMPACTOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.CLICKER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler());


    }


    @SubscribeEvent
    public static void addDeathStoneOnPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {

        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {

            if (event.isEndConquered()) return;

            ItemStack stack = new ItemStack(UtilityItems.DEATH_STONE.get());
            GlobalPos pos = player.getLastDeathLocation().orElse(new GlobalPos(Level.OVERWORLD, BlockPos.ZERO));
            stack.set(UtilityDataComponents.GLOBAL_POS.get(), pos);

            if (UtilityStartUpConfig.shouldPlayerGetDeathStoneOnDeath.get()) {
                ServerLevel serverLevel = (ServerLevel) level;


                if (Objects.requireNonNull(serverLevel.getGameRules().get(GameRules.KEEP_INVENTORY))) {
                    level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack));
                } else {
                    player.addItem(stack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(UtilityRecipeTypes.DRYING_TABLE_TYPE.get());
        event.sendRecipes(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get());
        event.sendRecipes(UtilityRecipeTypes.FLUID_GENERATOR_TYPE.get());
        event.sendRecipes(UtilityRecipeTypes.SUMMONING_TYPE.get());
    }

    @SubscribeEvent
    public static void onRecipeReceived(RecipesReceivedEvent event) {
        RecipeMap recipeMap = event.getRecipeMap();

        //Drying Table Recipes
        Collection<RecipeHolder<DryingTableRecipe>> dryingTableRecipes = recipeMap.byType(UtilityRecipeTypes.DRYING_TABLE_TYPE.get());
        Map<Identifier, DryingTableRecipe> dryingTableRecipeMap = new HashMap<>();

        for (RecipeHolder<DryingTableRecipe> holder : dryingTableRecipes) {
            dryingTableRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedDryingTableRecipes(dryingTableRecipeMap);

        //Resource Generator Recipes
        Collection<RecipeHolder<ResourceGeneratorRecipe>> resourceGeneratorRecipes = recipeMap.byType(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get());
        Map<Identifier, ResourceGeneratorRecipe> resourceGeneratorRecipeMap = new HashMap<>();

        for (RecipeHolder<ResourceGeneratorRecipe> holder : resourceGeneratorRecipes) {
            resourceGeneratorRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedResourceGeneratorRecipes(resourceGeneratorRecipeMap);

        //Fluid Generator Recipes
        Collection<RecipeHolder<FluidGeneratorRecipe>> fluidGeneratorRecipes = recipeMap.byType(UtilityRecipeTypes.FLUID_GENERATOR_TYPE.get());
        Map<Identifier, FluidGeneratorRecipe> fluidGeneratorRecipeMap = new HashMap<>();

        for (RecipeHolder<FluidGeneratorRecipe> holder : fluidGeneratorRecipes) {
            fluidGeneratorRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedFluidGeneratorRecipes(fluidGeneratorRecipeMap);

        //Summoning Recipes
        Collection<RecipeHolder<SummoningRecipe>> summoningRecipes = recipeMap.byType(UtilityRecipeTypes.SUMMONING_TYPE.get());
        Map<Identifier, SummoningRecipe> summoningRecipeMap = new HashMap<>();

        for (RecipeHolder<SummoningRecipe> holder : summoningRecipes) {
            summoningRecipeMap.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCachedSummoningRecipes(summoningRecipeMap);

        //Crafting Recipes
        Collection<RecipeHolder<CraftingRecipe>> craftingRecipes = recipeMap.byType(RecipeType.CRAFTING);

        Map<Item, List<CompressionRecipe>> result = new HashMap<>();

        for (RecipeHolder<CraftingRecipe> holder : craftingRecipes) {

            CraftingRecipe recipe = holder.value();

            if (!(recipe instanceof ShapedRecipe shaped)) continue;

            CompressionRecipe compression = convert(shaped);
            if (compression == null) continue;

            result
                    .computeIfAbsent(compression.item(), k -> new ArrayList<>())
                    .add(compression);
        }

        ClientRecipeCache.setCachedCompressionRecipes(result);

    }

    public static CompressionRecipe convert(ShapedRecipe recipe) {

        int width = recipe.pattern.width();
        int height = recipe.pattern.height();

        // ONLY allow 2x2 or 3x3 compression grids
        if (!((width == 2 && height == 2) || (width == 3 && height == 3))) {
            return null;
        }

        List<Optional<Ingredient>> ingredients = recipe.pattern.ingredients();
        if (ingredients.isEmpty()) return null;

        Item firstItem = null;

        for (Optional<Ingredient> optional : ingredients) {

            if (optional.isEmpty()) return null;

            Ingredient ingredient = optional.get();

            ItemStack[] stacks = ingredient.items()
                    .map(h -> new ItemStack(h.value()))
                    .toArray(ItemStack[]::new);

            if (stacks.length != 1) return null;

            Item item = stacks[0].getItem();

            if (firstItem == null) {
                firstItem = item;
            } else if (item != firstItem) {
                return null;
            }
        }

        if (firstItem == null) return null;

        boolean is3x3 = (width == 3 && height == 3);

        ItemStack input = new ItemStack(firstItem, width * height);

        CraftingInput craftingInput = getCraftingInput(input);
        if (craftingInput == null) return null;

        return new CompressionRecipe(firstItem, width * height, recipe.assemble(craftingInput), is3x3);
    }

    public static CraftingInput getCraftingInput(ItemStack stack) {
        if (stack.isEmpty()) return null;

        if (stack.count() >= 4 && stack.count() < 9) {
            return CraftingInput.of(2, 2, List.of(stack, stack, stack, stack));
        } else if (stack.count() >= 9) {
            return CraftingInput.of(3, 3, List.of(stack, stack, stack, stack, stack, stack, stack, stack, stack));
        } else {
            return null;
        }
    }
}
