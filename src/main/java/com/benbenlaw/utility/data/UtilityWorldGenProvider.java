package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.worldgen.UtilityBiomeModifiers;
import com.benbenlaw.utility.worldgen.UtilityConfiguredFeatures;
import com.benbenlaw.utility.worldgen.UtilityPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UtilityWorldGenProvider extends DatapackBuiltinEntriesProvider {

    public UtilityWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, UtilityConfiguredFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, UtilityPlacedFeatures::bootstrap)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, UtilityBiomeModifiers::bootstrap),
                Set.of(Utility.MOD_ID));
    }

}
