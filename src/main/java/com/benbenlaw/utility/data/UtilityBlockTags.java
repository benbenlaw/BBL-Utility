package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.util.UtilityTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class UtilityBlockTags extends BlockTagsProvider {

    UtilityBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Utility.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        //Sapling Grower
        tag(UtilityTags.Blocks.VALID_FOR_SAPLING_GROWER)
                .addTag(BlockTags.SAPLINGS)
                .add(Blocks.RED_MUSHROOM)
                .add(Blocks.BROWN_MUSHROOM)
        ;

        //Pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(UtilityBlocks.ENDER_ORE.get())
                .add(UtilityBlocks.DEEPSLATE_ENDER_ORE.get())
                .add(UtilityBlocks.BLOCK_BREAKER.get())
                .add(UtilityBlocks.BLOCK_PLACER.get())
                .add(UtilityBlocks.RESOURCE_GENERATOR.get())
                .add(UtilityBlocks.FLUID_COLLECTOR.get())
                .add(UtilityBlocks.FLUID_PLACER.get())
        ;

        //Axe
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(UtilityBlocks.DRYING_TABLE.get())
        ;



    }

    @Override
    public String getName() {
        return Utility.MOD_ID + " Block Tags";
    }
}
