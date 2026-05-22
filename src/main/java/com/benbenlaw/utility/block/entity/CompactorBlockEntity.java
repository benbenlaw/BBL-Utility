package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.CompactorBlock;
import com.benbenlaw.utility.event.client.ClientRecipeCache;
import com.benbenlaw.utility.recipe.custom.CompressionRecipe;
import com.benbenlaw.utility.screen.compactor.CompactorMenu;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompactorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;

    private static final int INPUT_START = 0;
    private static final int INPUT_END = 8;
    private static final int OUTPUT_START = 9;
    private static final int OUTPUT_END = 17;

    boolean is3x3 = false;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 18,
            (i, stack) -> i >= 0 && i <= 8,
            i -> i >= 9 && i <= 17
    );

    public CompactorBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.COMPACTOR_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CompactorBlockEntity.this.progress;
                    case 1 -> CompactorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CompactorBlockEntity.this.progress = value;
                    case 1 -> CompactorBlockEntity.this.maxProgress = value;
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

        if (!level.getBlockState(worldPosition).getValue(CompactorBlock.RUNNING)) {
            progress = 0;
            sync();
            return;
        }

        for (int i = INPUT_START; i <= INPUT_END; i++) {

            ItemStack stack = ItemUtil.getStack(inventory, i);
            if (stack.isEmpty()) continue;

            CompressionRecipe recipe = ClientRecipeCache.getCompressionRecipes(stack.getItem())
                    .stream()
                    .filter(r -> r.is3x3() == this.is3x3)
                    .findFirst()
                    .orElse(null);

            if (recipe == null) continue;
            if (recipe.is3x3() != this.is3x3) continue;
            if (stack.getCount() < recipe.inputCount()) continue;

            int outputSlot = findOutputSlot(recipe.output());
            if (outputSlot == -1) {
                progress = 0;
                sync();
                return;
            }

            progress++;

            if (progress >= maxProgress) {
                craftItem(i, outputSlot, recipe);
            }

            sync();
            return;
        }

        progress = 0;
        sync();
    }

    private void craftItem(int inputSlot, int outputSlot, CompressionRecipe recipe) {

        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.openRoot()) {

                inventory.extract(inputSlot, inventory.getResource(inputSlot), recipe.inputCount(), tx);
                inventory.insert(outputSlot, ItemResource.of(recipe.output()), recipe.output().getCount(), tx);
                tx.commit();
            }
        });

        assert level != null;

        level.playSound(null, worldPosition, SoundEvents.CRAFTER_CRAFT, SoundSource.BLOCKS, 0.4f, 1.0f);
        progress = 0;
        sync();
    }

    private int findOutputSlot(ItemStack output) {

        for (int i = OUTPUT_START; i <= OUTPUT_END; i++) {
            ItemStack stack = ItemUtil.getStack(inventory, i);

            if (stack.isEmpty()) return i;

            if (ItemStack.isSameItemSameComponents(stack, output)
                    && stack.getCount() + output.getCount() <= stack.getMaxStackSize()) {
                return i;
            }
        }

        return -1;
    }

    public ItemStacksResourceHandler getItemHandler() {
        return inventory;
    }

    public boolean is3x3() {
        return is3x3;
    }

    public void toggleCraftingMode() {
        is3x3 = !is3x3;
        sync();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new CompactorMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.compactor");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        inventory.serialize(output.child("inventory"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);
        output.putBoolean("is2x2First", is3x3);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inventory.deserialize(input.childOrEmpty("inventory"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);
        is3x3 = input.getBooleanOr("is2x2First", false);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
    }
}
