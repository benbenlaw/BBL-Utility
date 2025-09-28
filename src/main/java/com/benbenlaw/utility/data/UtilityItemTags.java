package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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

        tag(Tags.Items.TOOLS_SHEAR).add(UtilityItems.WOODEN_SHEARS.get());
        tag(Tags.Items.STRINGS).add(UtilityItems.LEAFY_STRING.get());

    }

    @Override
    public @NotNull String getName() {
        return Utility.MOD_ID + " Item Tags";
    }
}
