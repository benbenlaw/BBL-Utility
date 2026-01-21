package com.benbenlaw.utility.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;

//add to core

public class BlockTargetCodec {

    public static final Codec<BlockTarget> CODEC = Codec.either(
            BlockState.CODEC.xmap(BlockTarget.Single::new, BlockTarget.Single::blockState),
            TagKey.hashedCodec(Registries.BLOCK).xmap(BlockTarget.Tag::new, BlockTarget.Tag::tag))
            .xmap(
                    singleTagEither -> singleTagEither.map(
                            single -> (BlockTarget) single,
                            tag -> (BlockTarget) tag
                    ),
                    blockTarget -> {
                        if (blockTarget instanceof BlockTarget.Single single) return Either.left(single);
                        if (blockTarget instanceof BlockTarget.Tag tag) return Either.right(tag);
                        throw new IllegalStateException("Unknown BlockTarget type: " + blockTarget.getClass());
                    });

}
