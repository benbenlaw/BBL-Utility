package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.CoreFluidTank;
import com.benbenlaw.core.block.entity.FilterableBlockEntity;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.CoreFluidHandler;
import com.benbenlaw.core.block.entity.handler.FilterItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.FluidCollectorBlock;
import com.benbenlaw.utility.screen.collector.FluidCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidCollectorBlockEntity extends SyncableBlockEntity implements MenuProvider, FilterableBlockEntity {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;
    private final FluidStacksResourceHandler TANK = new FluidStacksResourceHandler(1, 16000) {
        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            super.onContentsChanged(index, previousContents);
        }
    };

    public final FluidTank TANK = new CoreFluidTank(this, 16000, "tank");
    private final IFluidHandler fluidHandler = new CoreFluidHandler(TANK);
    private boolean whitelist = true; // block entity owns it
    private FilterItemHandler filterHandler = new FilterItemHandler(1);

    public FluidCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.FLUID_COLLECTOR_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FluidCollectorBlockEntity.this.progress;
                    case 1 -> FluidCollectorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FluidCollectorBlockEntity.this.progress = value;
                    case 1 -> FluidCollectorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void tick() {
        assert level != null;
        if (!level.isClientSide()) {

            if (!level.getBlockState(worldPosition).getValue(FluidCollectorBlock.RUNNING)) return;

            BlockPos targetPos = worldPosition.relative(level.getBlockState(worldPosition).getValue(FluidCollectorBlock.FACING));
            Fluid fluidInWorld = level.getFluidState(targetPos).getType();

            if (TANK.getFluidAmount() > TANK.getCapacity() - 1000) return;
            if (!TANK.getFluid().is(fluidInWorld) && !TANK.isEmpty()) return;


            if (!level.getFluidState(targetPos).isEmpty() && filterHandler.allows(fluidInWorld.defaultFluidState())) {
                progress++;
                if (progress >= maxProgress) {
                    TANK.fill(new FluidStack(fluidInWorld, 1000), IFluidHandler.FluidAction.EXECUTE);
                    level.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                    progress = 0;
                    setChanged();
                    sync();
                }
            } else {
                progress = 0;
            }
        }
    }

    public boolean onPlayerUse(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, TANK);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new FluidCollectorMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.fluid_collector");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        TANK.serialize(output);
        filterHandler.serialize(output);
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        TANK.deserialize(input);
        filterHandler.deserialize(input);
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public boolean isWhitelist() {
        return whitelist;
    }

    @Override
    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
        setChanged();
        sync();
    }

    @Override
    public FilterItemHandler getFilterItemHandler() {
        return filterHandler;
    }
}
