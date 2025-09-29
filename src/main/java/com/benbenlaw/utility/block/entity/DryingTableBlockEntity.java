package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.IInventoryHandlingBlockEntity;
import com.benbenlaw.core.block.entity.handler.InputOutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.BlockPlacerBlock;
import com.benbenlaw.utility.block.custom.DryingTableBlock;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.screen.drying.DryingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DryingTableBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = UtilityStartUpConfig.dryingTableMaxDuration.get();
    private int progress = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            cachedRecipe = null;
            setChanged();
            sync();
        }
    };
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    private RecipeHolder<DryingTableRecipe> cachedRecipe;

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public IItemHandler getIItemHandler(Direction side) {
        return new InputOutputItemHandler(itemHandler,
                (i, stack) -> i == INPUT_SLOT,
                i -> i == OUTPUT_SLOT);
    }

    public DryingTableBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.DRYING_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> DryingTableBlockEntity.this.progress;
                    case 1 -> DryingTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> DryingTableBlockEntity.this.progress = value;
                    case 1 -> DryingTableBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void tick() {
        if (!level.isClientSide()) {

            if (!level.getBlockState(worldPosition).getValue(DryingTableBlock.RUNNING)) return;

            if (cachedRecipe == null) {
                updateCachedRecipe();
            }
            if (cachedRecipe != null && canInsertOutput(cachedRecipe.value().output())) {
                progress++;
                if (progress >= maxProgress) {
                    craftItem();
                }
            } else {
                progress = 0;
                setChanged();
            }
        }
    }

    private void craftItem() {
        if (cachedRecipe != null) {
            var recipe = cachedRecipe.value();
            itemHandler.extractItem(INPUT_SLOT, recipe.input().count(), false);
            itemHandler.insertItem(OUTPUT_SLOT, recipe.output().copy(), false);
            progress = 0;
            sync();
        }
    }

    private boolean canInsertOutput(ItemStack output) {
        ItemStack outputSlot = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (outputSlot.isEmpty()) {
            return true;
        } else if (!ItemStack.isSameItemSameComponents(outputSlot, output)) {
            return false;
        } else {
            int result = outputSlot.getCount() + output.getCount();
            return result <= output.getMaxStackSize();
        }
    }

    private void updateCachedRecipe() {
        if (level != null && level.getServer() != null) {
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.DRYING_TABLE_TYPE.get(),
                            new DryingTableRecipeInput(itemHandler, this.getBlockState().getValue(DryingTableBlock.WATERLOGGED)), level).orElse(null);
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new DryingTableMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        if (this.getBlockState().getValue(DryingTableBlock.WATERLOGGED)) {
            return Component.translatable("block.utility.soaking_table");
        } else {
            return Component.translatable("block.utility.drying_table");
        }
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
        maxProgress = input.getIntOr("maxProgress", UtilityStartUpConfig.dryingTableMaxDuration.get());
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
