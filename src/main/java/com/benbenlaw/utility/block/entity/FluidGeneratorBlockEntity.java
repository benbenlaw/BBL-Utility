package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.fluid.OutputFluidHandler;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.FluidGeneratorBlock;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.FluidGeneratorRecipe;
import com.benbenlaw.utility.screen.generator.FluidGeneratorMenu;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidGeneratorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;
    private final InputItemHandler inputHandler = new InputItemHandler(this, 1, (index, stack) -> true);

    private final OutputFluidHandler outputFluidHandler = new OutputFluidHandler(this,1,16000, i -> i == TANK_SLOT);

    public static final int INPUT_SLOT = 0;
    public static final int TANK_SLOT = 0;

    private RecipeHolder<FluidGeneratorRecipe> cachedRecipe;

    public FluidGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.FLUID_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FluidGeneratorBlockEntity.this.progress;
                    case 1 -> FluidGeneratorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FluidGeneratorBlockEntity.this.progress = value;
                    case 1 -> FluidGeneratorBlockEntity.this.maxProgress = value;
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

            if (!level.getBlockState(worldPosition).getValue(FluidGeneratorBlock.RUNNING)) return;

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

            if (!hasEnoughSpace(cachedRecipe.value().output())) return;

            if (cachedRecipe != null && canInsertOutput(cachedRecipe.value().output())) {
                progress++;
                if (progress >= maxProgress) {
                    craftItem();
                }
            } else {
                progress = 0;
            }
        }
    }

    private boolean hasEnoughSpace(FluidStack stack) {
        FluidResource current = outputFluidHandler.getResource(TANK_SLOT);
        return current.isEmpty() || (current.getFluid().isSame(stack.getFluid()) && outputFluidHandler.getAmountAsInt(TANK_SLOT) <= outputFluidHandler.getCapacityAsInt(TANK_SLOT, FluidResource.EMPTY) - stack.getAmount());
    }

    private void craftItem() {
        if (cachedRecipe != null) {
            var recipe = cachedRecipe.value();
            try (Transaction tx = Transaction.open(null)) {
                outputFluidHandler.insertInternal(TANK_SLOT, FluidResource.of(recipe.output()), recipe.output().getAmount(), tx);
                tx.commit();
            }
            progress = 0;
            sync();
        }
    }

    private boolean canInsertOutput(FluidStack output) {
        FluidStack outputTankFluid = outputFluidHandler.getResource(TANK_SLOT).toStack(TANK_SLOT);
        if (outputTankFluid.isEmpty()) {
            return true;
        } else if (!FluidStack.isSameFluidSameComponents(outputTankFluid, output)) {
            return false;
        } else {
            int result = outputTankFluid.getAmount() + output.getAmount();
            return result <= outputFluidHandler.getCapacityAsInt(TANK_SLOT, FluidResource.EMPTY);
        }
    }

    private void updateCachedRecipe() {
        if (level != null && level.getServer() != null) {
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.FLUID_GENERATOR_TYPE.get(),
                    new SingleRecipeInput(inputHandler.getResource(INPUT_SLOT).toStack()), level).orElse(null);
        }
    }

    public boolean onPlayerUse(Player player, InteractionHand hand, Direction direction) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.worldPosition, outputFluidHandler);
    }

    public InputItemHandler getInputItemHandler() {
        return inputHandler;
    }

    public OutputFluidHandler getOutputFluidHandler() {
        return outputFluidHandler;
    }

    public ResourceHandler<ItemResource> getItemCapability() {
        return inputHandler;
    }

    public ResourceHandler<FluidResource> getFluidCapability() {
        return outputFluidHandler;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new FluidGeneratorMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.fluid_generator");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        inputHandler.serialize(output.child("input"));
        outputFluidHandler.serialize(output.child("outputFluid"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inputHandler.deserialize(input.childOrEmpty("input"));
        outputFluidHandler.deserialize(input.childOrEmpty("outputFluid"));
        maxProgress = input.getIntOr("maxProgress", 200);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inputHandler);
    }
}
