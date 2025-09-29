package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.CoreFluidTank;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.CoreFluidHandler;
import com.benbenlaw.core.block.entity.handler.InputOutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.FluidPlacerBlock;
import com.benbenlaw.utility.screen.placer.FluidPlacerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidPlacerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;
    public final FluidTank TANK = new CoreFluidTank(this, 16000, "tank");
    private final IFluidHandler fluidHandler = new CoreFluidHandler(TANK);

    public FluidPlacerBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.FLUID_PLACER_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FluidPlacerBlockEntity.this.progress;
                    case 1 -> FluidPlacerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FluidPlacerBlockEntity.this.progress = value;
                    case 1 -> FluidPlacerBlockEntity.this.maxProgress = value;
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

            if (!level.getBlockState(worldPosition).getValue(FluidPlacerBlock.RUNNING)) return;

            if (TANK.isEmpty()) return;

            BlockPos targetPos = worldPosition.relative(level.getBlockState(worldPosition).getValue(FluidPlacerBlock.FACING));

            if (TANK.getFluid().getAmount() >= 1000 && level.getFluidState(targetPos).isEmpty() && level.getBlockState(targetPos).isEmpty()) {
                progress++;
                if (progress >= maxProgress) {
                    FluidUtil.tryPlaceFluid(null, level, InteractionHand.MAIN_HAND, targetPos, TANK, TANK.getFluid().copy());
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
        return new FluidPlacerMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.fluid_placer");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        TANK.serialize(output);
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        TANK.deserialize(input);
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

}
