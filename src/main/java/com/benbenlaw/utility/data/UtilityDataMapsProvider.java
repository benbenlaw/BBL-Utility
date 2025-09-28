package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class UtilityDataMapsProvider extends DataMapProvider {

    public UtilityDataMapsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerCompletableFuture) {
        super(output, providerCompletableFuture);
    }

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        var burnable = this.builder(NeoForgeDataMaps.FURNACE_FUELS);

        burnable.add(UtilityItems.MINI_COAL, new FurnaceFuel(200), false);
        burnable.add(UtilityItems.MINI_CHARCOAL, new FurnaceFuel(200), false);


    }

    @Override
    public @NotNull String getName() {
        return Utility.MOD_ID + " Data Maps";
    }
}
