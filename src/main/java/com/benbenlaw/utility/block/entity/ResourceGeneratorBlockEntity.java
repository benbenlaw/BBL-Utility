package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.CoreFluidTank;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.CoreFluidHandler;
import com.benbenlaw.core.block.entity.handler.InputOutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.BlockPlacerBlock;
import com.benbenlaw.utility.block.custom.DryingTableBlock;
import com.benbenlaw.utility.block.custom.ResourceGeneratorBlock;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.recipe.DryingTableRecipeInput;
import com.benbenlaw.utility.recipe.ResourceGeneratorRecipeInput;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.screen.drying.DryingTableMenu;
import com.benbenlaw.utility.screen.generator.ResourceGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
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
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceGeneratorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            cachedRecipe = null;
            setChanged();
            sync();
        }
    };

    public final FluidTank LEFT_TANK = new CoreFluidTank(this, 16000, "leftTank");
    public final FluidTank RIGHT_TANK = new CoreFluidTank(this, 16000, "rightTank");
    private final IFluidHandler fluidHandler = new CoreFluidHandler(LEFT_TANK, RIGHT_TANK);

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    private RecipeHolder<ResourceGeneratorRecipe> cachedRecipe;

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public IItemHandler getIItemHandler(Direction side) {
        return new InputOutputItemHandler(itemHandler,
                (i, stack) -> false,
                i -> i == OUTPUT_SLOT);
    }

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

            ItemStack output = recipe.output().copy();
            itemHandler.insertItem(OUTPUT_SLOT, output, false);

            if (recipe.consumeLeft()) {
                LEFT_TANK.drain(recipe.leftFluid(), IFluidHandler.FluidAction.EXECUTE);
            }
            if (recipe.consumeRight()) {
                RIGHT_TANK.drain(recipe.rightFluid(), IFluidHandler.FluidAction.EXECUTE);
            }
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
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.RESOURCE_GENERATOR_TYPE.get(),
                    new ResourceGeneratorRecipeInput(itemHandler, fluidHandler), level).orElse(null);
        }
    }

    public boolean onPlayerUse(Player player, InteractionHand hand, Direction direction) {
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return FluidUtil.interactWithFluidHandler(player, hand, LEFT_TANK);
        } else if (direction == Direction.EAST || direction == Direction.WEST) {
            return FluidUtil.interactWithFluidHandler(player, hand, RIGHT_TANK);
        }

        return false;
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

        itemHandler.serialize(output);
        LEFT_TANK.serialize(output);
        RIGHT_TANK.serialize(output);
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        itemHandler.deserialize(input);
        LEFT_TANK.deserialize(input);
        RIGHT_TANK.deserialize(input);
        maxProgress = input.getIntOr("maxProgress", 200);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            stacks.add(itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, stacks);
    }

}
