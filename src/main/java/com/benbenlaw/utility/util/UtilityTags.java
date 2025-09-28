package com.benbenlaw.utility.util;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.util.CoreTags;
import com.benbenlaw.utility.Utility;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class UtilityTags{

    public static class Blocks extends CoreTags.Blocks {
        public static final TagKey<Block> VALID_FOR_SAPLING_GROWER = tag(Utility.MOD_ID, "valid_for_sapling_grower");
    }
}
