package com.benbenlaw.utility.worldgen;

import com.benbenlaw.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class UtilityBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_OVERWORLD_ENDER_ORE =
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Utility.identifier("add_overworld_ender_ore"));

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {

        HolderGetter<Biome> biomeHolderGetter = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatureHolderGetter = context.lookup(Registries.PLACED_FEATURE);

        HolderSet.Named<Biome> overworldBiomes = biomeHolderGetter.getOrThrow(BiomeTags.IS_OVERWORLD);

        context.register(ADD_OVERWORLD_ENDER_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatureHolderGetter.getOrThrow(UtilityPlacedFeatures.OVERWORLD_ENDER_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

    }

}
