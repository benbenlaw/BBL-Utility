package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.util.UtilityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class UtilityEntityTags extends EntityTypeTagsProvider {

    public UtilityEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Utility.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //Placeholder
        tag(UtilityTags.Entities.CAN_BE_RELOCATED).add();

    }

    @Override
    public @NotNull String getName() {
        return Utility.MOD_ID + " Entity Tags";
    }
}
