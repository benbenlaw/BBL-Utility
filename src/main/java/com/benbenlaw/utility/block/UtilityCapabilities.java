package com.benbenlaw.utility.block;

import com.benbenlaw.utility.block.entity.*;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class UtilityCapabilities {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {


        //Todo: when i know what on earth im doing


        event.registerBlockEntity(Capabilities.Item.BLOCK,
                UtilityBlockEntities.DRYING_TABLE_BLOCK_ENTITY.get(), DryingTableBlockEntity::getIItemHandler);



        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.BLOCK_PLACER_BLOCK_ENTITY.get(), BlockPlacerBlockEntity::getIItemHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.BLOCK_BREAKER_BLOCK_ENTITY.get(), BlockBreakerBlockEntity::getIItemHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.RESOURCE_GENERATOR_BLOCK_ENTITY.get(), ResourceGeneratorBlockEntity::getIItemHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UtilityBlockEntities.ITEM_REPAIRER_BLOCK_ENTITY.get(), ItemRepairerBlockEntity::getIItemHandler);

         */

    }

}
