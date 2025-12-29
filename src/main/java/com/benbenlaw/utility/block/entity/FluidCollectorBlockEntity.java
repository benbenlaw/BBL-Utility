package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.WhitelistBlockEntity;
import com.benbenlaw.core.block.entity.handler.fluid.FilterFluidHandler;
import com.benbenlaw.core.block.entity.handler.fluid.OutputFluidHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.FluidCollectorBlock;
import com.benbenlaw.utility.screen.collector.FluidCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


//TODO re add filtering for fluids

public class FluidCollectorBlockEntity extends SyncableBlockEntity implements MenuProvider, WhitelistBlockEntity {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;

    private final OutputFluidHandler outputFluidHandler = new OutputFluidHandler(this,1,16000, i -> i == TANK_SLOT);
    public static final int TANK_SLOT = 0;
    private boolean whitelist = true;
    private FilterFluidHandler filterFluidHandler = new FilterFluidHandler(this, 1);

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
            FluidStack fluidStack = new FluidStack(fluidInWorld, 1000);

            if (outputFluidHandler.getAmountAsInt(TANK_SLOT) > outputFluidHandler.getCapacityAsInt(TANK_SLOT, FluidResource.EMPTY) - 1000) return;

            FluidResource current = outputFluidHandler.getResource(TANK_SLOT);
            if (!current.isEmpty() && !current.getFluid().isSame(fluidInWorld) /*&& !filter.isEmpty()*/) return;

            if (!level.getFluidState(targetPos).isEmpty() && filterFluidHandler.matchesFluid(fluidStack, whitelist)) {
                progress++;
                if (progress >= maxProgress) {

                    Fluid fluid = outputFluidHandler.getResource(TANK_SLOT).getFluid();
                    SoundEvent soundEvent = fluid.getFluidType().getSound(new FluidStack(fluid, 1000), SoundActions.BUCKET_FILL);
                    if (soundEvent == null)
                        soundEvent = SoundEvents.BUCKET_FILL;
                    level.playSound(null, worldPosition, soundEvent, SoundSource.BLOCKS, 0.4f, 1.0f);

                    try (Transaction tx = Transaction.open(null)) {
                        outputFluidHandler.insertInternal(TANK_SLOT, FluidResource.of(fluidInWorld), 1000, tx);
                        tx.commit();
                    }

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

    public OutputFluidHandler getFluidOutputHandler() {
        return outputFluidHandler;
    }

    public FilterFluidHandler getFilterFluidHandler() {
        return filterFluidHandler;
    }

    public ResourceHandler<FluidResource> getFluidCapability() {
        return outputFluidHandler;
    }

    public boolean onPlayerUse(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.worldPosition, outputFluidHandler);
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

        outputFluidHandler.serialize(output.child("outputFluid"));
        filterFluidHandler.serialize(output.child("fluidFilter"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        outputFluidHandler.deserialize(input.childOrEmpty("outputFluid"));
        filterFluidHandler.deserialize(input.childOrEmpty("fluidFilter"));
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
}
