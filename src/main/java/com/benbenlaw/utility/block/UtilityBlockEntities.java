package com.benbenlaw.utility.block;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class UtilityBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Utility.MOD_ID);

    public static final Supplier<BlockEntityType<DryingTableBlockEntity>> DRYING_TABLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("drying_table_block_entity", () ->
                    new BlockEntityType<>(DryingTableBlockEntity::new, UtilityBlocks.DRYING_TABLE.get()));

    public static final Supplier<BlockEntityType<BlockPlacerBlockEntity>> BLOCK_PLACER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("block_placer_block_entity", () ->
                    new BlockEntityType<>(BlockPlacerBlockEntity::new, UtilityBlocks.BLOCK_PLACER.get()));

    public static final Supplier<BlockEntityType<BlockBreakerBlockEntity>> BLOCK_BREAKER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("block_breaker_block_entity", () ->
                    new BlockEntityType<>(BlockBreakerBlockEntity::new, UtilityBlocks.BLOCK_BREAKER.get()));

    public static final Supplier<BlockEntityType<ResourceGeneratorBlockEntity>> RESOURCE_GENERATOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("resource_generator_block_entity", () ->
                    new BlockEntityType<>(ResourceGeneratorBlockEntity::new, UtilityBlocks.RESOURCE_GENERATOR.get()));

    public static final Supplier<BlockEntityType<FluidPlacerBlockEntity>> FLUID_PLACER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("fluid_placer_block_entity", () ->
                    new BlockEntityType<>(FluidPlacerBlockEntity::new, UtilityBlocks.FLUID_PLACER.get()));

    public static final Supplier<BlockEntityType<FluidCollectorBlockEntity>> FLUID_COLLECTOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("fluid_collector_block_entity", () ->
                    new BlockEntityType<>(FluidCollectorBlockEntity::new, UtilityBlocks.FLUID_COLLECTOR.get()));

    public static final Supplier<BlockEntityType<ItemRepairerBlockEntity>> ITEM_REPAIRER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("item_repairer_block_entity", () ->
                    new BlockEntityType<>(ItemRepairerBlockEntity::new, UtilityBlocks.ITEM_REPAIRER.get()));

}
