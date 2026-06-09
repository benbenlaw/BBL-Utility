package com.benbenlaw.utility.block;

import com.benbenlaw.casting.item.CastingItems;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.custom.*;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class UtilityBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Utility.MOD_ID);

    public static final DeferredBlock<Block> ENDER_ORE = registerBlock("ender_ore",
            properties -> new EnderOreBlock(properties
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(litBlockEmission())));

    public static final DeferredBlock<Block> DEEPSLATE_ENDER_ORE = registerBlock("deepslate_ender_ore",
            properties -> new EnderOreBlock(properties
                    .strength(4.5f, 3.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(litBlockEmission())));

    public static final DeferredBlock<Block> DRYING_TABLE = registerBlock("drying_table",
            properties -> new DryingTableBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> BLOCK_PLACER = registerBlock("block_placer",
    properties -> new BlockPlacerBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> BLOCK_BREAKER = registerBlock("block_breaker",
            properties -> new BlockBreakerBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RESOURCE_GENERATOR = registerBlock("resource_generator"
            , properties -> new ResourceGeneratorBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> FLUID_PLACER = registerBlock("fluid_placer",
            properties -> new FluidPlacerBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> FLUID_COLLECTOR = registerBlock("fluid_collector",
            properties -> new FluidCollectorBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> ITEM_REPAIRER = registerBlock("item_repairer",
            properties -> new ItemRepairerBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> FLUID_GENERATOR = registerBlock("fluid_generator",
            properties -> new FluidGeneratorBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> REDSTONE_CLOCK = registerBlock("redstone_clock",
            properties -> new RedstoneClockBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> ITEM_COLLECTOR = registerBlock("item_collector",
            properties -> new ItemCollectorBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> SUMMONING_BLOCK = registerBlock("summoning_block",
            properties -> new SummoningBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> COMPACTOR = registerBlock("compactor",
            properties -> new CompactorBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> CLICKER = registerBlock("clicker",
            properties -> new ClickerBlock(machineProperties(properties)));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        UtilityItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    private static ToIntFunction<BlockState> litBlockEmission() {
        return (lightLevel) -> lightLevel.getValue(BlockStateProperties.LIT) ? 9 : 0;
    }

    private static BlockBehaviour.Properties machineProperties(BlockBehaviour.Properties machineProperties) {
        return machineProperties
                .requiresCorrectToolForDrops()
                .strength(3.5f)
                .noOcclusion();
    }
}
