package com.benbenlaw.utility.block.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public class DryingTableRenderState extends BlockEntityRenderState {
    public BlockPos lightPosition;
    public FluidStack fluidStack;
    public int tankCapacity;
    public Level blockEntityLevel;
    public float rotation;

    final ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
}