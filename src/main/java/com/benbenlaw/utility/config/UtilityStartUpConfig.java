package com.benbenlaw.utility.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class UtilityStartUpConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> crookRolls;
    public static final ModConfigSpec.ConfigValue<Integer> crookDurability;
    public static final ModConfigSpec.ConfigValue<Boolean> crookTakesDamage;

    public static final ModConfigSpec.ConfigValue<Integer> totalGrowthAttempts;
    public static final ModConfigSpec.ConfigValue<Integer> saplingGrowerDurability;
    public static final ModConfigSpec.ConfigValue<Boolean> saplingGrowerTakesDamage;

    public static final ModConfigSpec.ConfigValue<Boolean> animalNetHostileMobs;
    public static final ModConfigSpec.ConfigValue<Boolean> animalNetWaterMobs;
    public static final ModConfigSpec.ConfigValue<Boolean> animalNetAnimalMobs;
    public static final ModConfigSpec.ConfigValue<Boolean> animalNetVillagerMobs;
    public static final ModConfigSpec.ConfigValue<Boolean> animalNetTakesDamage;
    public static final ModConfigSpec.ConfigValue<Integer> animalNetDurability;

    public static final ModConfigSpec.ConfigValue<Boolean> shouldPlayerGetDeathStoneOnDeath;

    public static final ModConfigSpec.ConfigValue<Integer> woodenShearsDurability;

    public static final ModConfigSpec.ConfigValue<Integer> dryingTableMaxDuration;


    static {
        BUILDER.comment("BBL Utility Startup Config");

        //Crook Configs
        BUILDER.push("Crook Configs");

        crookRolls = BUILDER.comment("Additional loot table rolls when using the crook on leaves, default = 3")
                .define("Additional Crook Rolls", 3);

        crookDurability = BUILDER.comment("The durability of the Crook, default = 64")
                .define("Crook Durability", 64);

        crookTakesDamage = BUILDER.comment("Does the Crook take damage when breaking leaves, default = true")
                .define("Crook Takes Damage", true);

        BUILDER.pop();

        //Sapling Grower
        BUILDER.comment("Sapling Grower Configs")
                .push("Sapling Grower");

        totalGrowthAttempts = BUILDER.comment("The number of attempts to grow a sapling per right click, default = 128")
                .define("Total Attempts for Sapling Grower", 128);

        saplingGrowerDurability = BUILDER.comment("The durability of the Sapling Grower, default = 128")
                .define("Sapling Grower Durability", 128);

        saplingGrowerTakesDamage = BUILDER.comment("Does the Sapling Grower take damage?, default = true")
                .define("Sapling Grower Takes Damage", true);

        BUILDER.pop();

        //Animal Net
        BUILDER.comment("Animal Net Configs")
                .push("Animal Net");

        animalNetHostileMobs = BUILDER.comment("Can the animal net capture hostile mobs, default = false")
                .define("Animal Net: Hostile Mobs", false);

        animalNetWaterMobs = BUILDER.comment("Can the animal net capture water mobs, default = true")
                .define("Animal Net: Water Mobs", true);

        animalNetAnimalMobs = BUILDER.comment("Can the animal net capture animal mobs, default = true")
                .define("Animal Net: Animal Mobs", true);

        animalNetVillagerMobs = BUILDER.comment("Can the animal net capture villager mobs, default = false")
                .define("Animal Net: Villager Mobs", false);

        animalNetTakesDamage = BUILDER.comment("Does the animal net take damage when capturing mobs, default = true")
                .define("Animal Net: Takes Damage?", true);

        animalNetDurability = BUILDER.comment("The durability of the Animal Net, default = 8")
                .define("Animal Net Durability", 8);

        BUILDER.pop();

        //Death Stone
        BUILDER.comment("Death Stone Configs")
                .push("Death Stone");

        shouldPlayerGetDeathStoneOnDeath = BUILDER.comment("Should the player get a death stone on death, default = true")
                .define("Death Stone: Get on Death", true);

        BUILDER.pop();

        //Wooden Shears
        BUILDER.comment("Wooden Shears Configs")
                .push("Wooden Shears");

        woodenShearsDurability = BUILDER.comment("The durability of the Wooden Shears, default = 16")
                .define("Wooden Shears Durability", 96);

        BUILDER.pop();

        //Drying Table
        BUILDER.comment("Drying Table Configs")
                .push("Drying Table");

        dryingTableMaxDuration = BUILDER.comment("The number of ticks it takes to dry/soak an item, default = 200 (10 seconds)")
                .define("Drying Recipe Duration", 200);

        BUILDER.pop();


        //End
        SPEC = BUILDER.build();

    }
}
