package com.benbenlaw.utility.util;

import com.benbenlaw.core.util.CoreTags;
import com.benbenlaw.utility.Utility;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class UtilityTags{

    public static class Blocks extends CoreTags.Blocks {
        public static final TagKey<Block> VALID_FOR_SAPLING_GROWER = tag(Utility.MOD_ID, "valid_for_sapling_grower");
        public static final TagKey<Block> HOT_BLOCKS = tag(Utility.MOD_ID, "hot_blocks");
        public static final TagKey<Block> COLD_BLOCKS = tag(Utility.MOD_ID, "cold_blocks");
    }

    public static class Items extends CoreTags.Items {
        public static final TagKey<Item> VALID_FOR_SAPLING_GROWER = tag(Utility.MOD_ID, "valid_for_sapling_grower");
        public static final TagKey<Item> HOT_BLOCKS = tag(Utility.MOD_ID, "hot_blocks");
        public static final TagKey<Item> COLD_BLOCKS = tag(Utility.MOD_ID, "cold_blocks");
    }

    public static class Entities {

        public static final TagKey<EntityType<?>> CAN_BE_RELOCATED = tag("can_be_relocated");

        private static TagKey<EntityType<?>> tag(String tagName) {
            return TagKey.create(Registries.ENTITY_TYPE, Utility.identifier(tagName));
        }
    }
}
