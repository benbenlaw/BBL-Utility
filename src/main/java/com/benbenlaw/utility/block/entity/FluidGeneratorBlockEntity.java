package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.CoreFluidTank;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.CoreFluidHandler;
import com.benbenlaw.core.block.entity.handler.InputOutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.FluidGeneratorBlock;
import com.benbenlaw.utility.block.custom.ResourceGeneratorBlock;
import com.benbenlaw.utility.recipe.ResourceGeneratorRecipeInput;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.FluidGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.screen.generator.FluidGeneratorMenu;
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
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidGeneratorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            cachedRecipe = null;
            setChanged();
            sync();
        }
    };

    public final FluidTank OUTPUT_TANK = new CoreFluidTank(this, 16000, "outputTank");
    private final IFluidHandler fluidHandler = new CoreFluidHandler(OUTPUT_TANK);

    public static final int INPUT_SLOT = 0;
    private RecipeHolder<FluidGeneratorRecipe> cachedRecipe;

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public IItemHandler getIItemHandler(Direction side) {
        return new InputOutputItemHandler(itemHandler,
                (i, stack) -> i == INPUT_SLOT,
                i -> false);
    }

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
            }
        }
    }

    private void craftItem() {
        if (cachedRecipe != null) {
            var recipe = cachedRecipe.value();

            OUTPUT_TANK.fill(recipe.output(), IFluidHandler.FluidAction.EXECUTE);
            progress = 0;
            sync();
        }
    }

    private boolean canInsertOutput(FluidStack output) {
        FluidStack outputTankFluid = OUTPUT_TANK.getFluid();
        if (outputTankFluid.isEmpty()) {
            return true;
        } else if (!FluidStack.isSameFluidSameComponents(outputTankFluid, output)) {
            return false;
        } else {
            int result = outputTankFluid.getAmount() + output.getAmount();
            return result <= OUTPUT_TANK.getCapacity();
        }
    }

    private void updateCachedRecipe() {
        if (level != null && level.getServer() != null) {
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.FLUID_GENERATOR_TYPE.get(),
                    new SingleRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level).orElse(null);
        }
    }

    public boolean onPlayerUse(Player player, InteractionHand hand, Direction direction) {
        return FluidUtil.interactWithFluidHandler(player, hand, OUTPUT_TANK);
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

        itemHandler.serialize(output);
        OUTPUT_TANK.serialize(output);
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        itemHandler.deserialize(input);
        OUTPUT_TANK.deserialize(input);
        maxProgress = input.getIntOr("maxProgress", 200);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(itemHandler);
    }
}
