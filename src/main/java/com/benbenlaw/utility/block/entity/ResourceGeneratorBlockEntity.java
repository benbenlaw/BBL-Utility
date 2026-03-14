package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.fluid.InputFluidHandler;
import com.benbenlaw.core.block.entity.handler.item.CombinedItemHandler;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.core.block.entity.handler.item.OutputItemHandler;
import com.benbenlaw.core.util.DirectionUtil;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ResourceGeneratorBlock;
import com.benbenlaw.utility.recipe.ResourceGeneratorRecipeInput;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.screen.generator.ResourceGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceGeneratorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;

    private final InputItemHandler inputHandler = new SingleInputItemHandler(this, 1, (index, stack) -> true);
    private final OutputItemHandler outputHandler = new OutputItemHandler(this,1, i -> i == OUTPUT_SLOT);
    private final InputFluidHandler inputFluidHandlerLeft = new InputFluidHandler(this,2,16000, (i, stack) -> i == LEFT_TANK_SLOT);
    private final InputFluidHandler inputFluidHandlerRight = new InputFluidHandler(this,2,16000, (i, stack) -> i == RIGHT_TANK_SLOT);

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 0;
    public static final int LEFT_TANK_SLOT = 0;
    public static final int RIGHT_TANK_SLOT = 0;
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

            ItemStack inputStack = inputHandler.getResource(INPUT_SLOT).toStack();

            if (inputStack.isEmpty()) {
                progress = 0;
                sync();
                cachedRecipe = null;
                return;
            }

            if (cachedRecipe != null && canInsertOutput(cachedRecipe.value().output().create())) {
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

            try (Transaction tx = Transaction.open(null)) {
                outputHandler.insertInternal(OUTPUT_SLOT, ItemResource.of(output), output.getCount(), tx);

                if (recipe.consumeLeft()) {
                    inputFluidHandlerLeft.extractInternal(LEFT_TANK_SLOT, FluidResource.of(recipe.leftFluid()), recipe.leftFluid().getAmount(), tx);
                }
                if (recipe.consumeRight()) {
                    inputFluidHandlerRight.extractInternal(RIGHT_TANK_SLOT, FluidResource.of(recipe.rightFluid()), recipe.rightFluid().getAmount(), tx);
                }
                tx.commit();
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
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get(),
                    new ResourceGeneratorRecipeInput(inputHandler, inputFluidHandlerLeft, inputFluidHandlerRight), level).orElse(null);
        }
    }

    public boolean onPlayerUse(Player player, InteractionHand hand, Direction direction) {

        if (direction == Direction.UP || direction == Direction.WEST || direction == Direction.NORTH) {
            return FluidUtil.interactWithFluidHandler(player, hand, this.worldPosition, inputFluidHandlerLeft);
        } else if (direction == Direction.DOWN || direction == Direction.EAST || direction == Direction.SOUTH) {
            return FluidUtil.interactWithFluidHandler(player, hand, this.worldPosition, inputFluidHandlerRight);
        }

        return false;
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

    public InputFluidHandler getInputFluidHandlerLeft() {
        return inputFluidHandlerLeft;
    }

    public InputFluidHandler getInputFluidHandlerRight() {
        return inputFluidHandlerRight;
    }

    public ResourceHandler<FluidResource> getFluidCapability(Direction direction) {

        if (direction == Direction.UP || direction == Direction.WEST || direction == Direction.NORTH) {
            return inputFluidHandlerLeft;
        } else if (direction == Direction.DOWN || direction == Direction.EAST || direction == Direction.SOUTH) {
            return inputFluidHandlerRight;
        }

        return null;
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

        inputHandler.serialize(output.child("input"));
        outputHandler.serialize(output.child("output"));
        inputFluidHandlerLeft.serialize(output.child("inputFluidLeft"));
        inputFluidHandlerRight.serialize(output.child("inputFluidRight"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inputHandler.deserialize(input.childOrEmpty("input"));
        outputHandler.deserialize(input.childOrEmpty("output"));
        inputFluidHandlerLeft.deserialize(input.childOrEmpty("inputFluidLeft"));
        inputFluidHandlerRight.deserialize(input.childOrEmpty("inputFluidRight"));
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
