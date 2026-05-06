package com.benbenlaw.utility.item;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.item.custom.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UtilityItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Utility.MOD_ID);

    //Simple items
    public static final DeferredItem<Item> MINI_COAL = ITEMS.registerSimpleItem("mini_coal");
    public static final DeferredItem<Item> MINI_CHARCOAL = ITEMS.registerSimpleItem("mini_charcoal");
    public static final DeferredItem<Item> LEAFY_STRING = ITEMS.registerSimpleItem("leafy_string");
    public static final DeferredItem<Item> SOAKED_PAPER = ITEMS.registerSimpleItem("soaked_paper");
    public static final DeferredItem<Item> LOG_SHEET = ITEMS.registerSimpleItem("log_sheet");
    public static final DeferredItem<Item> ENDER_PEARL_FRAGMENT = ITEMS.registerSimpleItem("ender_pearl_fragment");

    public static final DeferredItem<Item> DEATH_STONE = ITEMS.registerItem("death_stone", DeathStoneItem::new);
    public static final DeferredItem<Item> FLOATER = ITEMS.registerItem("floater", FloaterItem::new);

    //Configurable durability items
    public static final DeferredItem<Item> WOODEN_SHEARS = ITEMS.registerItem("wooden_shears",
            properties -> new ShearsItem(properties
                    .durability(UtilityStartUpConfig.woodenShearsDurability.get())
                    .enchantable(15)
                    .component(DataComponents.TOOL, ShearsItem.createToolProperties())));

    public static final DeferredItem<CrookItem> CROOK = ITEMS.registerItem("crook",
            CrookItem::new, properties -> properties
                    .durability(UtilityStartUpConfig.crookDurability.get())
                    .enchantable(15)
                    .component(DataComponents.TOOL, CrookItem.createToolProperties()));

    public static final DeferredItem<SaplingGrowerItem> SAPLING_GROWER = ITEMS.registerItem("sapling_grower",
            SaplingGrowerItem::new, properties -> properties
                    .enchantable(15)
                    .durability(UtilityStartUpConfig.saplingGrowerDurability.get()));

    public static final DeferredItem<AnimalNetItem> ANIMAL_NET = ITEMS.registerItem("animal_net",
            AnimalNetItem::new, properties -> properties
                    .enchantable(15)
                    .durability(UtilityStartUpConfig.animalNetDurability.get()));




}
