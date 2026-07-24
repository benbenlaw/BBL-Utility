package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.util.UtilityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class UtilityBlockTags extends BlockTagsProvider {

    UtilityBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Utility.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        //Doorbell Blocks
        tag(UtilityTags.Blocks.DOORBELL_BLOCKS)
                .addTag(BlockTags.WOODEN_BUTTONS)
        ;

        //Hot Blocks
        tag(UtilityTags.Blocks.HOT_BLOCKS)
                .add(Blocks.MAGMA_BLOCK)
                .add(Blocks.LAVA)
                .addOptionalTag(TagKey.create(Registries.BLOCK, Identifier.parse("c:storage_blocks/uranium")))
        ;

        //Cold Blocks
        tag(UtilityTags.Blocks.COLD_BLOCKS)
                .add(Blocks.ICE)
                .add(Blocks.PACKED_ICE)
                .add(Blocks.BLUE_ICE)
                .add(Blocks.SNOW)
                .add(Blocks.SNOW_BLOCK)
                .add(Blocks.POWDER_SNOW)
        ;

        //Sapling Grower
        tag(UtilityTags.Blocks.VALID_FOR_SAPLING_GROWER)
                .addTag(BlockTags.SAPLINGS)
                .add(Blocks.RED_MUSHROOM)
                .add(Blocks.BROWN_MUSHROOM)
        ;

        //Pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(UtilityBlocks.ENDER_ORE.get())
                .add(UtilityBlocks.ENDER_ORE.get())
                .add(UtilityBlocks.DEEPSLATE_ENDER_ORE.get())
                .add(UtilityBlocks.BLOCK_BREAKER.get())
                .add(UtilityBlocks.BLOCK_PLACER.get())
                .add(UtilityBlocks.RESOURCE_GENERATOR.get())
                .add(UtilityBlocks.FLUID_COLLECTOR.get())
                .add(UtilityBlocks.FLUID_PLACER.get())
                .add(UtilityBlocks.FLUID_GENERATOR.get())
                .add(UtilityBlocks.ITEM_COLLECTOR.get())
                .add(UtilityBlocks.ITEM_REPAIRER.get())
                .add(UtilityBlocks.REDSTONE_CLOCK.get())
                .add(UtilityBlocks.SUMMONING_BLOCK.get())
                .add(UtilityBlocks.COMPACTOR.get())
                .add(UtilityBlocks.DRYING_TABLE.get())
        ;



    }

    @Override
    public String getName() {
        return Utility.MOD_ID + " Block Tags";
    }
}
