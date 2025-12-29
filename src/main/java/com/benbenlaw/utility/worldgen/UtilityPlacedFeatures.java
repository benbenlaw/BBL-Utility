package com.benbenlaw.utility.worldgen;

import com.benbenlaw.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class UtilityPlacedFeatures {

    public static final ResourceKey<PlacedFeature> OVERWORLD_ENDER_ORE_PLACED_KEY = createKey("overworld_ender_ore_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);

        Holder<ConfiguredFeature<?, ?>> overworldEnderOreConfiguredFeature =
                holdergetter.getOrThrow(UtilityConfiguredFeatures.OVERWORLD_ENDER_ORE_KEY);

        PlacementUtils.register(context, OVERWORLD_ENDER_ORE_PLACED_KEY, holdergetter.getOrThrow(UtilityConfiguredFeatures.OVERWORLD_ENDER_ORE_KEY),
                commonOrePlacement(8, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(40))));


    }

    private static List<PlacementModifier> orePlacement(PlacementModifier modifier, PlacementModifier modifier1) {
        return List.of(modifier, InSquarePlacement.spread(), modifier1, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
        return orePlacement(CountPlacement.of(count), modifier);
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Utility.identifier(name));
    }
}
