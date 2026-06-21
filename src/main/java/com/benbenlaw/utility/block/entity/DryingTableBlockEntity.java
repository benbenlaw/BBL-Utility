package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.fluid.SyncableFluidHandler;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.DryingTableBlock;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.item.FluidListComponent;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.screen.drying.DryingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DryingTableBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = UtilityStartUpConfig.dryingTableMaxDuration.get();
    private int progress = 0;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 2,
            (i, stack) -> i == INPUT_SLOT,
            i -> i == OUTPUT_SLOT
    );

    private final SyncableFluidHandler fluidInventory = new SyncableFluidHandler(this, 1, 16000,
            (i, stack) -> true,
            i -> true
    );

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    private RecipeHolder<DryingTableRecipe> cachedRecipe;

    private ItemStack lastInputSnapshot = ItemStack.EMPTY;

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

        ItemStack inputStack = ItemUtil.getStack(inventory, INPUT_SLOT);

        if (inputStack.isEmpty()) {
            progress = 0;
            sync();
            cachedRecipe = null;
            lastInputSnapshot = ItemStack.EMPTY;
            return;
        }

        if (cachedRecipe == null || !ItemStack.isSameItemSameComponents(inputStack, lastInputSnapshot)) {
            updateCachedRecipe();
            lastInputSnapshot = inputStack.copy();
        }

        boolean canCraft = cachedRecipe != null
                && hasSufficientFluid(cachedRecipe.value())
                && canInsertOutput(cachedRecipe.value().output().create());

        if (canCraft) {
            progress++;
            if (progress >= maxProgress) craftItem();
        } else {
            progress = 0;
            sync();
        }
    }

    private boolean hasSufficientFluid(DryingTableRecipe recipe) {
        if (recipe.fluid().isEmpty()) {
            return true;
        }

        FluidStack actual = FluidUtil.getStack(fluidInventory, 0);

        var requiredFluid = recipe.fluid().get();
        return requiredFluid.is(actual.getFluidType()) && actual.getAmount() >= requiredFluid.amount();
    }

    private void craftItem() {
        if (cachedRecipe != null) {
            var recipe = cachedRecipe.value();

            inventory.runInternal(() -> {
                try (Transaction tx = Transaction.openRoot()) {
                    inventory.extract(INPUT_SLOT, inventory.getResource(INPUT_SLOT), recipe.input().count(), tx);
                    inventory.insert(OUTPUT_SLOT, ItemResource.of(recipe.output()), recipe.output().create().getCount(), tx);
                    tx.commit();
                }
            });

            fluidInventory.runInternal(() -> {
                if (recipe.fluid().isPresent() && recipe.consumeAmount().isPresent()) {
                    try (Transaction tx = Transaction.openRoot()) {
                        fluidInventory.extract(0, FluidResource.of(FluidUtil.getStack(fluidInventory, 0)), recipe.consumeAmount().get(), tx);
                        tx.commit();
                    }
                }
            });

            assert level != null;
            if (recipe.fluid().isPresent()) {
                level.playSound(null, worldPosition, SoundEvents.MUD_PLACE, SoundSource.BLOCKS, 0.4f, 1.0f);
            } else {
                level.playSound(null, worldPosition, SoundEvents.DRY_GRASS, SoundSource.BLOCKS, 0.4f, 1.0f);
            }

            cachedRecipe = null;
            lastInputSnapshot = ItemStack.EMPTY;

            progress = 0;
            sync();
        }
    }

    private boolean canInsertOutput(ItemStack output) {
        ItemStack outputSlot = ItemUtil.getStack(inventory, OUTPUT_SLOT);
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
                    new DryingTableRecipeInput(inventory, fluidInventory), level
            ).orElse(null);
        }
    }

    public ItemStacksResourceHandler getItemHandler() {
        return inventory;
    }

    public FluidStacksResourceHandler getFluidHandler() {
        return fluidInventory;
    }

    public boolean onPlayerUse(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.worldPosition, fluidInventory);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new DryingTableMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        if (!fluidInventory.getResource(0).isEmpty()) {
            return Component.translatable("block.utility.soaking_table", FluidUtil.getStack(fluidInventory, 0).getHoverName());
        } else {
            return Component.translatable("block.utility.drying_table");
        }
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        inventory.serialize(output.child("inventory"));
        fluidInventory.serialize(output.child("fluidInventory"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inventory.deserialize(input.childOrEmpty("inventory"));
        fluidInventory.deserialize(input.childOrEmpty("fluidInventory"));
        maxProgress = input.getIntOr("maxProgress", UtilityStartUpConfig.dryingTableMaxDuration.get());
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
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