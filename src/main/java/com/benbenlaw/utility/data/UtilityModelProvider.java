package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class UtilityModelProvider extends ModelProvider {

    public UtilityModelProvider(PackOutput output) {
        super(output, Utility.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        //Items
        itemModels.generateFlatItem(UtilityItems.CROOK.get(), ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        itemModels.generateFlatItem(UtilityItems.SAPLING_GROWER.get(), ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        itemModels.generateFlatItem(UtilityItems.ANIMAL_NET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.DEATH_STONE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.FLOATER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.MINI_COAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.MINI_CHARCOAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.WOODEN_SHEARS.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(UtilityItems.LEAFY_STRING.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.SOAKED_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.LOG_SHEET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.ENDER_PEARL_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);

        //Blocks
        blockModels.createTrivialCube(UtilityBlocks.ENDER_ORE.get());
        blockModels.createTrivialCube(UtilityBlocks.DEEPSLATE_ENDER_ORE.get());


    }

    @Override
    protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return UtilityBlocks.BLOCKS.getEntries().stream().filter(x ->
                !x.is(UtilityBlocks.DRYING_TABLE.getId()) &&
                !x.is(UtilityBlocks.BLOCK_PLACER.getId()) &&
                !x.is(UtilityBlocks.BLOCK_BREAKER.getId()) &&
                !x.is(UtilityBlocks.FLUID_COLLECTOR.getId()) &&
                !x.is(UtilityBlocks.FLUID_PLACER.getId()) &&
                !x.is(UtilityBlocks.RESOURCE_GENERATOR.getId())
        );
    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return UtilityItems.ITEMS.getEntries().stream();
    }

    @Override
    public @NotNull String getName() {
        return Utility.MOD_ID + " Models";
    }
}
