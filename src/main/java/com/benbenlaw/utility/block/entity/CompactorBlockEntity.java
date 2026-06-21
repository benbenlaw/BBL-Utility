package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.CompactorBlock;
import com.benbenlaw.utility.screen.compactor.CompactorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CompactorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;

    private static final int INPUT_START = 0;
    private static final int INPUT_END = 8;
    private static final int OUTPUT_START = 9;
    private static final int OUTPUT_END = 17;

    private record RecipeCacheKey(Item item, DataComponentPatch components) {}

    private static final Map<RecipeCacheKey, Optional<RecipeHolder<CraftingRecipe>>> RECIPE_CACHE_2X2 = new HashMap<>();
    private static final Map<RecipeCacheKey, Optional<RecipeHolder<CraftingRecipe>>> RECIPE_CACHE_3X3 = new HashMap<>();

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

        int needed = requiredCount();

        for (int i = INPUT_START; i <= INPUT_END; i++) {

            ItemStack stack = ItemUtil.getStack(inventory, i);
            if (stack.isEmpty()) continue;
            if (stack.getCount() < needed) continue;

            CraftingInput input = buildCraftingInput(stack);
            RecipeHolder<CraftingRecipe> recipe = getRecipeForInput(stack, input);
            if (recipe == null) continue;

            ItemStack output = recipe.value().assemble(input);
            if (output.isEmpty()) continue;

            int outputSlot = findOutputSlot(output);
            if (outputSlot == -1) {
                progress = 0;
                sync();
                return;
            }

            progress++;

            if (progress >= maxProgress) {
                craftItem(i, outputSlot, output, needed);
            }

            sync();
            return;
        }

        progress = 0;
        sync();
    }

    private int requiredCount() {
        return is3x3 ? 9 : 4;
    }

    private CraftingInput buildCraftingInput(ItemStack stack) {
        int size = is3x3 ? 3 : 2;

        List<ItemStack> items = new ArrayList<>(size * size);
        for (int i = 0; i < size * size; i++) {
            items.add(stack.copyWithCount(1));
        }

        return CraftingInput.of(size, size, items);
    }

    @Nullable
    private RecipeHolder<CraftingRecipe> getRecipeForInput(ItemStack stack, CraftingInput input) {

        Map<RecipeCacheKey, Optional<RecipeHolder<CraftingRecipe>>> cache = is3x3 ? RECIPE_CACHE_3X3 : RECIPE_CACHE_2X2;

        RecipeCacheKey key = new RecipeCacheKey(stack.getItem(), stack.getComponentsPatch());

        Optional<RecipeHolder<CraftingRecipe>> cached = cache.get(key);
        if (cached != null) {
            return cached.orElse(null);
        }

        assert level != null;

        Optional<RecipeHolder<CraftingRecipe>> found = level.getServer().getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, level);

        cache.put(key, found);
        return found.orElse(null);
    }

    public static void clearRecipeCache() {
        RECIPE_CACHE_2X2.clear();
        RECIPE_CACHE_3X3.clear();
    }

    private void craftItem(int inputSlot, int outputSlot, ItemStack output, int consumeCount) {

        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.openRoot()) {

                inventory.extract(inputSlot, inventory.getResource(inputSlot), consumeCount, tx);
                inventory.insert(outputSlot, ItemResource.of(output), output.getCount(), tx);
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
        progress = 0;
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