package com.benbenlaw.utility.recipe;

import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class DryingTableRecipeInput implements RecipeInput {

    private final InputItemHandler handler;
    private final boolean isWaterlogged;

    public DryingTableRecipeInput(InputItemHandler handler, boolean isWaterlogged) {
        this.handler = handler;
        this.isWaterlogged = isWaterlogged;
    }

    @Override
    public ItemStack getItem(int i) {
        return handler.getResource(i).toStack();
    }

    @Override
    public int size() {
        return handler.size();
    }

    public boolean isWaterlogged() {
        return isWaterlogged;
    }


}
