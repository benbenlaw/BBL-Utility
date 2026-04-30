package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.fluid.SyncableFluidHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.FluidPlacerBlock;
import com.benbenlaw.utility.item.FluidListComponent;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.screen.placer.FluidPlacerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidPlacerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;
    private final SyncableFluidHandler fluidInventory = new SyncableFluidHandler(this,1, 16000,
            (i, stack) -> true,
            i -> false);

    public static final int TANK_SLOT = 0;

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

            if (fluidInventory.getAmountAsInt(TANK_SLOT) == 0) return;

            BlockPos targetPos = worldPosition.relative(level.getBlockState(worldPosition).getValue(FluidPlacerBlock.FACING));

            if (fluidInventory.getAmountAsInt(TANK_SLOT) >= 1000 && level.getFluidState(targetPos).isEmpty() && level.getBlockState(targetPos).isEmpty()) {
                progress++;
                if (progress >= maxProgress) {

                    Fluid fluid = fluidInventory.getResource(TANK_SLOT).getFluid();
                    SoundEvent soundEvent = fluid.getFluidType().getSound(new FluidStack(fluid, 1000), SoundActions.BUCKET_EMPTY);
                    if (soundEvent == null)
                        soundEvent = SoundEvents.BUCKET_EMPTY;
                    level.playSound(null, worldPosition, soundEvent, SoundSource.BLOCKS, 0.4f, 1.0f);

                    level.setBlockAndUpdate(targetPos, fluidInventory.getResource(TANK_SLOT).getFluid().defaultFluidState().createLegacyBlock());

                    fluidInventory.runInternal(() -> {
                        try (Transaction tx = Transaction.open(null)) {
                            fluidInventory.extract(TANK_SLOT, FluidResource.of(fluidInventory.getResource(TANK_SLOT).getFluid()), 1000, tx);
                            tx.commit();
                        }
                    });

                    progress = 0;
                    setChanged();
                    sync();
                }
            } else {
                progress = 0;
            }
        }
    }

    public FluidStacksResourceHandler getFluidHandler() {
        return fluidInventory;
    }

    public boolean onPlayerUse(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.worldPosition, fluidInventory);
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

        fluidInventory.serialize(output.child("fluidInventory"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        fluidInventory.deserialize(input.childOrEmpty("fluidInventory"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(UtilityDataComponents.FLUIDS.get(),
                FluidListComponent.fromHandlers(fluidInventory));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        FluidListComponent component = components.get(UtilityDataComponents.FLUIDS.get());
        if (component != null) {
            component.applyToHandlers(fluidInventory);
        }
    }

}
