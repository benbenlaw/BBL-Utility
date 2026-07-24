package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.util.UtilityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class UtilityItemTags extends ItemTagsProvider {

    public UtilityItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Utility.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //Doorbell Blocks
        tag(UtilityTags.Items.DOORBELL_BLOCKS)
                .addTag(ItemTags.WOODEN_BUTTONS)
        ;


        //Enchantments
        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(UtilityItems.CROOK.get())
                .add(UtilityItems.SAPLING_GROWER.get())
                .add(UtilityItems.ANIMAL_NET.get())
                .add(UtilityItems.WOODEN_SHEARS.get())
        ;

        tag(Tags.Items.ENCHANTABLES)
                .add(UtilityItems.CROOK.get())
                .add(UtilityItems.SAPLING_GROWER.get())
                .add(UtilityItems.ANIMAL_NET.get())
                .add(UtilityItems.WOODEN_SHEARS.get())
        ;

        //Hot Blocks
        tag(UtilityTags.Items.HOT_BLOCKS)
                .add(Items.MAGMA_BLOCK)
                ;

        //Cold Blocks
        tag(UtilityTags.Items.COLD_BLOCKS)
                .add(Items.ICE)
                .add(Items.PACKED_ICE)
                .add(Items.BLUE_ICE)
                .add(Items.SNOW)
        ;

        //Sapling Grower
        tag(UtilityTags.Items.VALID_FOR_SAPLING_GROWER)
                .addTag(ItemTags.SAPLINGS)
                .add(Items.RED_MUSHROOM)
                .add(Items.BROWN_MUSHROOM)
        ;

        tag(Tags.Items.TOOLS_SHEAR).add(UtilityItems.WOODEN_SHEARS.get());
        tag(Tags.Items.STRINGS).add(UtilityItems.LEAFY_STRING.get());

    }

    @Override
    public @NotNull String getName() {
        return Utility.MOD_ID + " Item Tags";
    }
}
