package com.benbenlaw.utility.recipe;

import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.utility.util.TemperatureValues;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nullable;

public class SummoningRecipeInput implements RecipeInput {

    private final ItemStacksResourceHandler handler;
    private final BlockState belowBlock;
    private final TemperatureValues temperatureValues;

    public SummoningRecipeInput(ItemStacksResourceHandler handler, BlockState belowBlock, TemperatureValues temperatureValues) {
        this.handler = handler;
        this.belowBlock = belowBlock;
        this.temperatureValues = temperatureValues;
    }

    @Override
    public ItemStack getItem(int i) {
        return handler.getResource(i).toStack();
    }

    @Override
    public int size() {
        return handler.size();
    }

    public BlockState getBelowBlock() {
        return belowBlock;
    }

    public @Nullable TemperatureValues getTemperatureVariant() {
        return temperatureValues;
    }
}
