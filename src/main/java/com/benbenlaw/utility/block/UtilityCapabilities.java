package com.benbenlaw.utility.block;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class UtilityCapabilities {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.BLOCK_PLACER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemCapability()
        );

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.BLOCK_BREAKER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemCapability()
        );

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.DRYING_TABLE_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemCapability()
        );

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.RESOURCE_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemCapability()
        );

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.RESOURCE_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidCapability(side)
        );

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.ITEM_REPAIRER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemCapability()
        );

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.FLUID_COLLECTOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidOutputHandler()
        );

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.FLUID_PLACER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidCapability()
        );

        event.registerBlockEntity(Capabilities.Item.BLOCK, UtilityBlockEntities.FLUID_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getItemCapability()
        );

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, UtilityBlockEntities.FLUID_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getFluidCapability()
        );
    }
}
