package com.benbenlaw.utility.recipe;

import com.benbenlaw.core.block.entity.handler.fluid.InputFluidHandler;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;


public class ResourceGeneratorRecipeInput implements RecipeInput {

    private final InputItemHandler handler;
    private final InputFluidHandler fluidHandlerLeft;
    private final InputFluidHandler fluidHandlerRight;

    public ResourceGeneratorRecipeInput(InputItemHandler handler, InputFluidHandler fluidHandlerLeft, InputFluidHandler fluidHandlerRight) {
        this.handler = handler;
        this.fluidHandlerLeft = fluidHandlerLeft;
        this.fluidHandlerRight = fluidHandlerRight;
    }

    @Override
    public ItemStack getItem(int i) {
        return handler.getResource(i).toStack();
    }

    @Override
    public int size() {
        return handler.size();
    }

    public FluidStack getLeftFluid() {
        return fluidHandlerLeft.copyToList().getFirst();
    }

    public FluidStack getRightFluid() {
        return fluidHandlerRight.copyToList().getFirst();
    }


}
