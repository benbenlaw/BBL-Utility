package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.RedstoneClockBlock;
import com.benbenlaw.utility.screen.clock.RedstoneClockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneClockBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress;
    private int onTime;
    private int signalStrength;
    private boolean emittingRedstoneSignal = false;
    private int progress = 0;
    int emittingProgress = 0;

    public RedstoneClockBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.REDSTONE_CLOCK_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> RedstoneClockBlockEntity.this.progress;
                    case 1 -> RedstoneClockBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> RedstoneClockBlockEntity.this.progress = value;
                    case 1 -> RedstoneClockBlockEntity.this.maxProgress = value;
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

            if (!level.getBlockState(worldPosition).getValue(RedstoneClockBlock.RUNNING) && !emittingRedstoneSignal) return;

            if (!emittingRedstoneSignal) {
                progress++;
                if (progress >= maxProgress) {
                    emittingRedstoneSignal = true;
                    emittingProgress = 0;
                    level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                }
            } else {
                emittingProgress++;
                if (emittingProgress >= onTime) {
                    emittingRedstoneSignal = false;
                    progress = 0;
                    emittingProgress = 0;
                    level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                }
            }
        }
    }

    public void setMaxProgress(int newMaxProgress) {
        maxProgress = newMaxProgress;
        sync();
        setChanged();
    }

    public void setOnTime(int newOnTime) {
        onTime = newOnTime;
        sync();
        setChanged();
    }

    public void setSignalStrength(int newSignalStrength) {
        signalStrength = newSignalStrength;
        sync();
        assert level != null;
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        setChanged();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getOnTime() {
        return onTime;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public boolean isEmittingRedstoneSignal() {
        return emittingRedstoneSignal;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RedstoneClockMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.redstone_clock");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        output.putInt("maxProgress", maxProgress);
        output.putInt("onTime", onTime);
        output.putInt("signalStrength", signalStrength);
        output.putInt("progress", progress);
        output.putInt("emittingProgress", emittingProgress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        maxProgress = input.getIntOr("maxProgress", 200);
        onTime = input.getIntOr("onTime", 10);
        signalStrength = input.getIntOr("signalStrength", 15);
        progress = input.getIntOr("progress", 0);
        emittingProgress = input.getIntOr("emittingProgress", 0);

        super.loadAdditional(input);
    }
}
