package com.benbenlaw.utility.block;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.custom.*;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class UtilityBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Utility.MOD_ID);

    public static final DeferredBlock<Block> ENDER_ORE = registerBlock("ender_ore",
            () -> new EnderOreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_ORE)
                    .lightLevel(litBlockEmission()).setId(createID("ender_ore"))));

    public static final DeferredBlock<Block> DEEPSLATE_ENDER_ORE = registerBlock("deepslate_ender_ore",
            () -> new EnderOreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_REDSTONE_ORE)
                    .lightLevel(litBlockEmission()).setId(createID("deepslate_ender_ore"))));

    public static final DeferredBlock<Block> DRYING_TABLE = registerBlock("drying_table",
            () -> new DryingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .noOcclusion().setId(createID("drying_table"))));

    public static final DeferredBlock<Block> BLOCK_PLACER = registerBlock("block_placer",
            () -> new BlockPlacerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .noOcclusion().setId(createID("block_placer"))));

    public static final DeferredBlock<Block> BLOCK_BREAKER = registerBlock("block_breaker",
            () -> new BlockBreakerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .noOcclusion().setId(createID("block_breaker"))));

    public static final DeferredBlock<Block> RESOURCE_GENERATOR = registerBlock("resource_generator",
            () -> new ResourceGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .noOcclusion().setId(createID("resource_generator"))));

    public static final DeferredBlock<Block> FLUID_PLACER = registerBlock("fluid_placer",
            () -> new FluidPlacerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .noOcclusion().setId(createID("fluid_placer"))));

    public static final DeferredBlock<Block> FLUID_COLLECTOR = registerBlock("fluid_collector",
            () -> new FluidCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .noOcclusion().setId(createID("fluid_collector"))));

    public static final DeferredBlock<Block> ITEM_REPAIRER = registerBlock("item_repairer",
            () -> new ItemRepairerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .noOcclusion().setId(createID("item_repairer"))));





    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }


    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        UtilityItems.ITEMS.registerItem(name, (properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    private static ToIntFunction<BlockState> litBlockEmission() {
        return (lightLevel) -> lightLevel.getValue(BlockStateProperties.LIT) ? 9 : 0;
    }

    public static ResourceKey<Block> createID(String name) {
        return ResourceKey.create(Registries.BLOCK, Utility.rl(name));
    }
}
