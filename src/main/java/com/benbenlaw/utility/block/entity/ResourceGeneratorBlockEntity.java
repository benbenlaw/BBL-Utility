package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.fluid.SyncableFluidHandler;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.core.util.DirectionUtil;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ResourceGeneratorBlock;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.item.FluidListComponent;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.recipe.ResourceGeneratorRecipeInput;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.screen.generator.ResourceGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceGeneratorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = UtilityStartUpConfig.resourceGeneratorMaxDuration.get();
    private int progress = 0;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 2,
            (i, stack) -> i == INPUT_SLOT,
            i -> i == OUTPUT_SLOT
    );

    private final SyncableFluidHandler fluidInventory = new SyncableFluidHandler(this, 2, 16000,
            (i, stack) -> true,
            i -> true
    );

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int LEFT_TANK_SLOT = 0;
    public static final int RIGHT_TANK_SLOT = 1;

    private RecipeHolder<ResourceGeneratorRecipe> cachedRecipe;

    public ResourceGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.RESOURCE_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ResourceGeneratorBlockEntity.this.progress;
                    case 1 -> ResourceGeneratorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ResourceGeneratorBlockEntity.this.progress = value;
                    case 1 -> ResourceGeneratorBlockEntity.this.maxProgress = value;
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

            if (!level.getBlockState(worldPosition).getValue(ResourceGeneratorBlock.RUNNING)) return;

            if (cachedRecipe == null) {
                updateCachedRecipe();
            }

            ItemStack inputStack = ItemUtil.getStack(inventory, INPUT_SLOT);

            if (inputStack.isEmpty()) {
                progress = 0;
                sync();
                cachedRecipe = null;
                return;
            }

            if (cachedRecipe != null) {
                ItemStack result = cachedRecipe.value().output().create();

                if (!canInsertOutput(result)) {
                    progress = 0;
                    sync();
                    return;
                }

                progress++;
                if (progress >= maxProgress) {
                    craftItem();
                }
            } else {
                progress = 0;
            }
        }
    }

    private void craftItem() {
        if (cachedRecipe != null) {
            var recipe = cachedRecipe.value();

            ItemStack output = recipe.output().create().copy();

            inventory.runInternal(() -> {
                try (Transaction tx = Transaction.open(null)) {
                    inventory.insert(OUTPUT_SLOT, ItemResource.of(output), output.getCount(), tx);
                    tx.commit();
                }
            });

            fluidInventory.runInternal(() -> {
                try (Transaction tx = Transaction.open(null)) {
                    if (recipe.consumeLeft()) {
                        fluidInventory.extract(LEFT_TANK_SLOT, FluidResource.of(recipe.leftFluid()), recipe.leftFluid().amount(), tx);
                    }
                    if (recipe.consumeRight()) {
                        fluidInventory.extract(RIGHT_TANK_SLOT, FluidResource.of(recipe.rightFluid()), recipe.rightFluid().amount(), tx);
                    }
                    tx.commit();
                }
            });

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
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get(),
                    new ResourceGeneratorRecipeInput(inventory, fluidInventory), level).orElse(null);
        }
    }

    public boolean onPlayerUse(Player player, InteractionHand hand, Direction direction) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.worldPosition, fluidInventory);
    }

    public ItemStacksResourceHandler getItemHandler() {
        return inventory;
    }

    public FluidStacksResourceHandler getFluidHandler() {
        return fluidInventory;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new ResourceGeneratorMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.resource_generator");
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
        maxProgress = input.getIntOr("maxProgress", 200);
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
