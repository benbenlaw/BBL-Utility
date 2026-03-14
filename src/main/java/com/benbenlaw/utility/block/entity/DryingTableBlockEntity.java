package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.CombinedItemHandler;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.core.block.entity.handler.item.OutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.DryingTableBlock;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.screen.drying.DryingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.client.event.sound.SoundEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DryingTableBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = UtilityStartUpConfig.dryingTableMaxDuration.get();
    private int progress = 0;

    private final InputItemHandler inputHandler = new InputItemHandler(this,1, (i, stack) -> i == INPUT_SLOT);
    private final OutputItemHandler outputHandler = new OutputItemHandler(this,1, i -> i == OUTPUT_SLOT);

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 0;
    private RecipeHolder<DryingTableRecipe> cachedRecipe;

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
        assert level != null;
        if (level.isClientSide()) return;

        boolean running = level.getBlockState(worldPosition).getValue(DryingTableBlock.RUNNING);
        if (!running) {
            progress = 0;
            sync();
            return;
        }

        ItemStack inputStack = inputHandler.getResource(INPUT_SLOT).toStack();

        if (inputStack.isEmpty()) {
            progress = 0;
            sync();
            cachedRecipe = null;
            return;
        }

        if (cachedRecipe == null) {
            updateCachedRecipe();
        }

        if (cachedRecipe != null && canInsertOutput(cachedRecipe.value().output().create())) {
            progress++;
            if (progress >= maxProgress) craftItem();
        } else {
            progress = 0;
            sync();
        }
    }


    private void craftItem() {
        if (cachedRecipe != null) {
            var recipe = cachedRecipe.value();
            try (Transaction tx = Transaction.open(null)) {
                inputHandler.extractInternal(INPUT_SLOT, inputHandler.getResource(INPUT_SLOT), recipe.input().count(), tx);
                outputHandler.insertInternal(OUTPUT_SLOT, ItemResource.of(recipe.output()), recipe.output().create().getCount(), tx);
                tx.commit();
            }

            assert level != null;
            if (recipe.recipeType() == DryingTableRecipeType.SOAKING) {
                level.playSound(null, worldPosition, SoundEvents.MUD_PLACE, SoundSource.BLOCKS, 0.4f, 1.0f);
            } else {
                level.playSound(null, worldPosition, SoundEvents.DRY_GRASS, SoundSource.BLOCKS, 0.4f, 1.0f);
            }

            progress = 0;
            sync();
        }
    }

    private boolean canInsertOutput(ItemStack output) {
        ItemStack outputSlot = outputHandler.getResource(OUTPUT_SLOT).toStack();
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
            boolean isWaterlogged = this.getBlockState().getValue(DryingTableBlock.WATERLOGGED);
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.DRYING_TABLE_TYPE.get(),
                    new DryingTableRecipeInput(inputHandler, isWaterlogged), level
            ).orElse(null);
        }
    }


    public InputItemHandler getInputHandler() {
        return inputHandler;
    }

    public OutputItemHandler getOutputHandler() {
        return outputHandler;
    }

    public ResourceHandler<ItemResource> getItemCapability() {
        return new CombinedItemHandler(inputHandler, outputHandler);
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

        inputHandler.serialize(output.child("input"));
        outputHandler.serialize(output.child("output"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inputHandler.deserialize(input.childOrEmpty("input"));
        outputHandler.deserialize(input.childOrEmpty("output"));
        maxProgress = input.getIntOr("maxProgress", UtilityStartUpConfig.dryingTableMaxDuration.get());
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inputHandler);
        dropInventoryContents(outputHandler);
    }
}
