package com.benbenlaw.utility.block;

import com.benbenlaw.utility.block.entity.BlockBreakerBlockEntity;
import com.benbenlaw.utility.block.entity.BlockPlacerBlockEntity;
import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class UtilityCapabilities {


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.DRYING_TABLE_BLOCK_ENTITY.get(), DryingTableBlockEntity::getIItemHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.BLOCK_PLACER_BLOCK_ENTITY.get(), BlockPlacerBlockEntity::getIItemHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.BLOCK_BREAKER_BLOCK_ENTITY.get(), BlockBreakerBlockEntity::getIItemHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.RESOURCE_GENERATOR_BLOCK_ENTITY.get(), ResourceGeneratorBlockEntity::getIItemHandler);


    }

}
