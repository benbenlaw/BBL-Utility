package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.BlockPlacerBlock;
import com.benbenlaw.utility.screen.placer.BlockPlacerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockPlacerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;
    private final InputItemHandler inputHandler = new InputItemHandler(this, 1, (i, stack) -> i == INPUT_SLOT);

    public static final int INPUT_SLOT = 0;

    public BlockPlacerBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.BLOCK_PLACER_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> BlockPlacerBlockEntity.this.progress;
                    case 1 -> BlockPlacerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> BlockPlacerBlockEntity.this.progress = value;
                    case 1 -> BlockPlacerBlockEntity.this.maxProgress = value;
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

            if (!level.getBlockState(worldPosition).getValue(BlockPlacerBlock.RUNNING)) return;

            if (inputHandler.getResource(INPUT_SLOT).toStack().getItem() instanceof BlockItem blockItem) {

                BlockHitResult rayTrace = new BlockHitResult(worldPosition.getCenter(), getBlockState().getValue(BlockPlacerBlock.FACING), worldPosition, false);
                BlockPlaceContext blockPlaceContext = new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, inputHandler.getResource(INPUT_SLOT).toStack(), rayTrace);

                if (blockPlaceContext.canPlace()) {
                    progress++;
                    if (progress >= maxProgress) {
                        if (blockItem.place(blockPlaceContext).consumesAction()) {
                            try (Transaction tx = Transaction.open(null)) {
                                inputHandler.extractInternal(INPUT_SLOT, inputHandler.getResource(INPUT_SLOT), 1, tx);
                                tx.commit();
                            }
                        }
                        progress = 0;
                        sync();
                    }
                }
            } else {
                progress = 0;
                sync();
            }
        }
    }

    public InputItemHandler getInputHandler() {
        return inputHandler;
    }

    public ResourceHandler<ItemResource> getItemCapability() {
        return inputHandler;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new BlockPlacerMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.block_placer");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        inputHandler.serialize(output.child("input"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inputHandler.deserialize(input.childOrEmpty("input"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inputHandler);
    }
}
