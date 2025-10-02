package com.benbenlaw.utility.data;

import com.benbenlaw.utility.Utility;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class UtilityLangProvider extends LanguageProvider {

    public UtilityLangProvider(PackOutput output) {
        super(output, Utility.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.utility", "BBL Utility");

        //Blocks
        add("block.utility.ender_ore", "Ender Ore");
        add("block.utility.deepslate_ender_ore", "Deepslate Ender Ore");
        add("block.utility.drying_table", "Drying Table");
        add("block.utility.soaking_table", "Soaking Table");
        add("block.utility.block_placer", "Block Placer");
        add("block.utility.block_breaker", "Block Breaker");
        add("block.utility.resource_generator", "Resource Generator");
        add("block.utility.fluid_placer", "Fluid Placer");
        add("block.utility.fluid_collector", "Fluid Collector");

        //Items
        add("item.utility.crook", "Crook");
        add("item.utility.sapling_grower", "Sapling Grower");
        add("item.utility.animal_net", "Animal Net");
        add("item.utility.death_stone", "Death Stone");
        add("item.utility.floater", "Floater");
        add("item.utility.mini_coal", "Mini Coal");
        add("item.utility.mini_charcoal", "Mini Charcoal");
        add("item.utility.wooden_shears", "Wooden Shears");
        add("item.utility.leafy_string", "Leafy String");
        add("item.utility.soaked_paper", "Soaked Paper");
        add("item.utility.log_sheet", "Log Sheet");
        add("item.utility.ender_pearl_fragment", "Ender Pearl Fragment");

        //Tooltips - Info
        add("tooltip.crook.info", "Breaks leaves faster and increase leaves drops rates");
        add("tooltip.sapling_grower.info", "Used to grow saplings instantly");
        add("tooltip.animal_net.info", "Used to catch and release mobs");
        add("tooltip.death_stone.info", "Given upon death, right click to return to your death location");
        add("tooltip.floater.info", "When in your off hand allows you to place a block in your main hand in the air or water");

        //Animal Net Tooltips
        add("tooltip.animal_net.mob_types", "Can Catch:");
        add("tooltip.animal_net.hostile_mobs", "- Hostile Mobs");
        add("tooltip.animal_net.water_mobs", "- Water Mobs");
        add("tooltip.animal_net.animal_mobs", "- Animal Mobs");
        add("tooltip.animal_net.villager_mobs", "- Villager Mobs");
        add("tooltip.animal_net.contains", "Contains - %s");
        add("tooltip.animal_net.unknown_mob", "Unknown Mob");
        add("tooltip.animal_net.could_not_release", "Could not release mob!");
        add("tooltip.animal_net.mob_cannot_be_caught", "This mob cannot be caught!");
        add("tooltip.animal_net.mob_caught", "I just caught a %s!");

        //Death Stone Tooltips
        add("tooltip.death_stone.returning", "Returning to the crime scene");

        //Redstone Clock Tooltips
        add("tooltip.redstone_clock.max_progress", "Time between pluses in ticks");
        add("tooltip.redstone_clock.on_time", "Pulse in ticks");
        add("tooltip.redstone_clock.signal_strength", "Signal Strength (0-15)");

        //JEI
        add("jei.utility.drying_table", "Drying Table");
        add("jei.utility.soaking", "Drying Table must be Waterlogged");
        add("jei.utility.resource_generator", "Resource Generator");
        add("jei.utility.consumed", "Fluid Consumed");
    }

    @Override
    public @NotNull String getName() {
        return Utility.MOD_ID + " Language Provider";
    }
}
