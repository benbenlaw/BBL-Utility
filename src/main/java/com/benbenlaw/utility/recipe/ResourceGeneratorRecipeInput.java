package com.benbenlaw.utility.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ResourceGeneratorRecipeInput implements RecipeInput {

    private final ItemStackHandler handler;
    private final IFluidHandler fluidHandler;

    public ResourceGeneratorRecipeInput(ItemStackHandler handler, IFluidHandler fluidHandler) {
        this.handler = handler;
        this.fluidHandler = fluidHandler;
    }

    @Override
    public ItemStack getItem(int i) {
        return handler.getStackInSlot(i);
    }

    @Override
    public int size() {
        return handler.getSlots();
    }

    public FluidStack getLeftFluid() {
        return fluidHandler.getFluidInTank(0);
    }

    public FluidStack getRightFluid() {
        return fluidHandler.getFluidInTank(1);
    }


}
