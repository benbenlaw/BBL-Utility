package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.InputOutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ItemRepairerBlock;
import com.benbenlaw.utility.screen.repairer.ItemRepairerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRepairerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            sync();
        }
    };
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public IItemHandler getIItemHandler(Direction side) {
        return new InputOutputItemHandler(itemHandler,
                (i, stack) -> i == INPUT_SLOT,
                i -> i == OUTPUT_SLOT);
    }

    public ItemRepairerBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.ITEM_REPAIRER_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ItemRepairerBlockEntity.this.progress;
                    case 1 -> ItemRepairerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ItemRepairerBlockEntity.this.progress = value;
                    case 1 -> ItemRepairerBlockEntity.this.maxProgress = value;
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

            if (!level.getBlockState(worldPosition).getValue(ItemRepairerBlock.RUNNING)) return;
            if (!itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()) return;

            if (itemHandler.getStackInSlot(INPUT_SLOT).isDamageableItem()) {

                ItemStack tool = itemHandler.getStackInSlot(INPUT_SLOT);
                int damage = tool.getDamageValue();
                int maxDamage = tool.getMaxDamage();
                maxProgress = maxDamage - damage;
                progress++;

                if (progress >= maxProgress) {
                    tool.setDamageValue(-maxDamage);
                    itemHandler.setStackInSlot(OUTPUT_SLOT, tool.copy());
                    itemHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
                    progress = 0;
                    maxProgress = 200;
                }
            } else {
                progress = 0;
            }
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new ItemRepairerMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.item_repairer");
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
        maxProgress = input.getIntOr("maxProgress", 200);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(itemHandler);
    }
}
