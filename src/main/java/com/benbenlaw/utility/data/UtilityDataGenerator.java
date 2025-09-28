package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class UtilityDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new UtilityBlockTags(packOutput, lookupProvider));
        generator.addProvider(true, new UtilityLangProvider(packOutput));
        generator.addProvider(true, new UtilityDataMapsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(UtilityLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        generator.addProvider(true, new UtilityModelProvider(packOutput));
        generator.addProvider(true, new UtilityRecipeProvider.Runner (packOutput, lookupProvider));



    }

}
