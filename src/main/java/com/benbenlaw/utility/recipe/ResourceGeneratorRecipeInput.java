package com.benbenlaw.utility.recipe;

import com.benbenlaw.core.block.entity.handler.fluid.SyncableFluidHandler;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;


public class ResourceGeneratorRecipeInput implements RecipeInput {

    private final SyncableItemHandler handler;
    private final SyncableFluidHandler fluidHandler;

    public ResourceGeneratorRecipeInput(SyncableItemHandler handler, SyncableFluidHandler fluidHandlerLeft) {
        this.handler = handler;
        this.fluidHandler = fluidHandlerLeft;
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
        return FluidUtil.getStack(fluidHandler, ResourceGeneratorBlockEntity.LEFT_TANK_SLOT);
    }

    public FluidStack getRightFluid() {
        return FluidUtil.getStack(fluidHandler, ResourceGeneratorBlockEntity.RIGHT_TANK_SLOT);
    }


}
