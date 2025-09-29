package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.InputOutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.BlockPlacerBlock;
import com.benbenlaw.utility.screen.placer.BlockPlacerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockPlacerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            sync();
        }
    };
    public static final int INPUT_SLOT = 0;

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public IItemHandler getIItemHandler(Direction side) {
        return new InputOutputItemHandler(itemHandler,
                (i, stack) -> i == INPUT_SLOT,
                i -> false);
    }

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

            if (itemHandler.getStackInSlot(INPUT_SLOT).getItem() instanceof BlockItem blockItem) {

                BlockHitResult rayTrace = new BlockHitResult(worldPosition.getCenter(), getBlockState().getValue(BlockPlacerBlock.FACING), worldPosition, false);
                BlockPlaceContext blockPlaceContext = new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, itemHandler.getStackInSlot(INPUT_SLOT), rayTrace);

                if (blockPlaceContext.canPlace()) {
                    progress++;
                    if (progress >= maxProgress) {

                        blockItem.place(blockPlaceContext);
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

        itemHandler.serialize(output);
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        itemHandler.deserialize(input);
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            stacks.add(itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, stacks);
    }
}
