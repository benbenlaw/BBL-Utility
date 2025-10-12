package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.CombinedItemHandler;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.core.block.entity.handler.item.OutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ItemRepairerBlock;
import com.benbenlaw.utility.screen.repairer.ItemRepairerMenu;
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
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRepairerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;

    private final InputItemHandler inputHandler = new InputItemHandler(this,1, (i, stack) -> i == INPUT_SLOT);
    private final OutputItemHandler outputHandler = new OutputItemHandler(this,1, i -> i == OUTPUT_SLOT);

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 0;

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
            if (!outputHandler.getResource(OUTPUT_SLOT).isEmpty()) return;

            if (inputHandler.getResource(INPUT_SLOT).toStack().isDamageableItem()) {

                ItemStack tool = inputHandler.getResource(INPUT_SLOT).toStack();
                int damage = tool.getDamageValue();
                int maxDamage = tool.getMaxDamage();
                maxProgress = maxDamage - damage;
                progress++;

                if (progress >= maxProgress) {
                    tool.setDamageValue(-maxDamage);
                    outputHandler.set(OUTPUT_SLOT, ItemResource.of(tool.copy()), 1);
                    try (Transaction tx = Transaction.open(null)) {
                        inputHandler.extractInternal(INPUT_SLOT, inputHandler.getResource(INPUT_SLOT), 1, tx);
                        tx.commit();
                    }
                    level.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.4f, 1.0f);
                    progress = 0;
                    maxProgress = 200;
                }
            } else {
                progress = 0;
            }
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
        return new ItemRepairerMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.item_repairer");
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
        maxProgress = input.getIntOr("maxProgress", 200);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inputHandler);
        dropInventoryContents(outputHandler);
    }
}
