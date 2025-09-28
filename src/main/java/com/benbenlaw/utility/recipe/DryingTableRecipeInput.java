package com.benbenlaw.utility.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DryingTableRecipeInput implements RecipeInput {

    private final ItemStackHandler handler;
    private final boolean isWaterlogged;

    public DryingTableRecipeInput(ItemStackHandler handler, boolean isWaterlogged) {
        this.handler = handler;
        this.isWaterlogged = isWaterlogged;
    }

    @Override
    public ItemStack getItem(int i) {
        return handler.getStackInSlot(i);
    }

    @Override
    public int size() {
        return handler.getSlots();
    }

    public boolean isWaterlogged() {
        return isWaterlogged;
    }


}
