package com.benbenlaw.utility.recipe;

import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.NonNull;

public class DryingTableRecipeInput implements RecipeInput {

    private final ItemStacksResourceHandler handler;
    private final FluidStacksResourceHandler fluidHandler;

    public DryingTableRecipeInput(ItemStacksResourceHandler handler, FluidStacksResourceHandler fluidHandler) {
        this.handler = handler;
        this.fluidHandler = fluidHandler;
    }

    @Override
    public @NonNull ItemStack getItem(int i) {
        return handler.getResource(i).toStack();
    }

    @Override
    public int size() {
        return handler.size();
    }

    public FluidStack getFluid() {
        return FluidUtil.getStack(fluidHandler, 0);
    }


}
