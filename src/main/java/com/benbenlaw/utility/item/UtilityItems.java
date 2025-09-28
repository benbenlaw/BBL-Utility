package com.benbenlaw.utility.item;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.item.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UtilityItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Utility.MOD_ID);

    public static final DeferredItem<CrookItem> CROOK = ITEMS.registerItem("crook",
            properties -> new CrookItem(new Item.Properties().durability(UtilityStartUpConfig.crookDurability.get())
                    .setId(createID("crook"))));

    public static final DeferredItem<SaplingGrowerItem> SAPLING_GROWER = ITEMS.registerItem("sapling_grower",
            properties -> new SaplingGrowerItem(new Item.Properties().durability(UtilityStartUpConfig.saplingGrowerDurability.get())
                    .setId(createID("sapling_grower"))));

    public static final DeferredItem<AnimalNetItem> ANIMAL_NET = ITEMS.registerItem("animal_net",
            properties -> new AnimalNetItem(new Item.Properties().durability(UtilityStartUpConfig.animalNetDurability.get())
                    .setId(createID("animal_net"))));

    public static final DeferredItem<DeathStoneItem> DEATH_STONE = ITEMS.registerItem("death_stone",
            properties -> new DeathStoneItem(new Item.Properties().setId(createID("death_stone"))));

    public static final DeferredItem<FloaterItem> FLOATER = ITEMS.registerItem("floater",
            properties -> new FloaterItem(new Item.Properties().setId(createID("floater"))));

    public static final DeferredItem<Item> MINI_COAL = ITEMS.registerItem("mini_coal",
            properties -> new Item(new Item.Properties().setId(createID("mini_coal"))));

    public static final DeferredItem<Item> MINI_CHARCOAL = ITEMS.registerItem("mini_charcoal",
            properties -> new Item(new Item.Properties().setId(createID("mini_charcoal"))));

    public static final DeferredItem<Item> WOODEN_SHEARS = ITEMS.registerItem("wooden_shears",
            properties -> new ShearsItem(new Item.Properties().durability(UtilityStartUpConfig.woodenShearsDurability.get()).setId(createID("wooden_shears"))));

    public static final DeferredItem<Item> LEAFY_STRING = ITEMS.registerItem("leafy_string",
            properties -> new Item(new Item.Properties().setId(createID("leafy_string"))));

    public static final DeferredItem<Item> SOAKED_PAPER = ITEMS.registerItem("soaked_paper",
            properties -> new Item(new Item.Properties().setId(createID("soaked_paper"))));

    public static final DeferredItem<Item> LOG_SHEET = ITEMS.registerItem("log_sheet",
            properties -> new Item(new Item.Properties().setId(createID("log_sheet"))));

    public static final DeferredItem<Item> ENDER_PEARL_FRAGMENT = ITEMS.registerItem("ender_pearl_fragment",
            properties -> new Item(new Item.Properties().setId(createID("ender_pearl_fragment"))));




    public static ResourceKey<Item> createID(String name) {
        return ResourceKey.create(Registries.ITEM, Utility.rl(name));
    }
}
