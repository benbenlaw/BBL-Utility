package com.benbenlaw.utility.event;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.event.client.ClientRecipeCache;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.recipe.custom.FluidGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.SummoningRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class UtilityEvents {

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
                (blockEntity, side) -> blockEntity.getItemHandler()
        );
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

    }
}
