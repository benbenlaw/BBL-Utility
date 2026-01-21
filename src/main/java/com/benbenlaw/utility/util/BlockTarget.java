package com.benbenlaw.utility.util;


import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

//Add to core
public sealed interface BlockTarget permits BlockTarget.Single, BlockTarget.Tag {

    boolean matches(BlockState state, boolean ignoreBlockState);

    default Optional<Block> getBlock() {
        return Optional.empty();
    }

    default Optional<TagKey<Block>> getTag() {
        return Optional.empty();
    }

    record Single(BlockState blockState) implements BlockTarget {
        @Override
        public boolean matches(BlockState state, boolean ignoreBlockState) {
            if (state.getBlock() != blockState.getBlock()) return false;

            if (ignoreBlockState) {
                return true;
            } else {
                // Compare only the non-default properties
                BlockState defaultState = blockState.getBlock().defaultBlockState();
                for (Property<?> property : blockState.getProperties()) {
                    Comparable<?> recipeValue = blockState.getValue(property);
                    Comparable<?> defaultValue = defaultState.getValue(property);
                    Comparable<?> levelValue = state.getValue(property);

                    if (!recipeValue.equals(defaultValue)) {
                        if (!recipeValue.equals(levelValue)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        @Override
        public Optional<Block> getBlock() {
            return Optional.of(blockState.getBlock());
        }
    }

    record Tag(TagKey<Block> tag) implements BlockTarget {
        @Override
        public boolean matches(BlockState state, boolean ignoreBlockState) {
            return state.is(tag);
        }

        @Override
        public Optional<TagKey<Block>> getTag() {
            return Optional.of(tag);
        }
    }
}