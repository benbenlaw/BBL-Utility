package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.WhitelistBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.FilterItemHandler;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ItemCollectorBlock;
import com.benbenlaw.utility.screen.collector.ItemCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCollectorBlockEntity extends SyncableBlockEntity implements MenuProvider, WhitelistBlockEntity {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;

    private int leftRightOffset = 0;
    private int upDownOffset = 0;
    private int forwardBackOffset = 0;

    private int width = 1;
    private int height = 1;
    private int depth = 1;

    public static final int MAX_SIZE = 9;
    public static final int MAX_OFFSET = 9;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 9,
            (i, stack) -> false,
            i -> true
    );

    private boolean whitelist = true;
    private final FilterItemHandler filterHandler = new FilterItemHandler(this,8);
    public static final int[] OUTPUT_SLOTS = {0,1,2,3,4,5,6,7,8};

    public ItemCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.ITEM_COLLECTOR_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ItemCollectorBlockEntity.this.progress;
                    case 1 -> ItemCollectorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ItemCollectorBlockEntity.this.progress = value;
                    case 1 -> ItemCollectorBlockEntity.this.maxProgress = value;
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
            if (!level.getBlockState(worldPosition).getValue(ItemCollectorBlock.RUNNING)) return;

            AABB box = null;
            box = createArea();
            if (box == null) return;

            progress++;

            if (progress >= maxProgress) {
                progress = 0;
                List<Entity> entityList = level.getEntities(null, box);

                for (Entity entity : entityList) {
                    if (!(entity instanceof ItemEntity itemEntity)) continue;

                    ItemStack stack = itemEntity.getItem();
                    if (stack.isEmpty()) continue;

                    if (!filterHandler.matchesItem(stack, whitelist)) continue;

                    ItemResource resource = ItemResource.of(stack);

                    boolean fullyInserted = false;

                    for (int i = 0; i < inventory.size(); i++) {
                        ItemStack outputSlot = inventory.getResource(i).toStack();

                        if (!outputSlot.isEmpty() && !ItemStack.isSameItemSameComponents(outputSlot, stack)) continue;

                        int finalI = i;
                        inventory.runInternal(() -> {
                            try (Transaction tx = Transaction.open(null)) {
                                int inserted = inventory.insert(finalI, resource, stack.getCount(), tx);
                                if (inserted > 0) {
                                    tx.commit();
                                    stack.shrink(inserted);
                                    if (stack.isEmpty()) itemEntity.discard();
                                }
                            }
                        });
                    }
                    if (!fullyInserted) itemEntity.setItem(stack);
                }
            }
        }
    }

    public AABB createArea() {
        BlockPos center = getAreaCenter();
        Direction facing = getBlockState().getValue(ItemCollectorBlock.FACING);

        if (!facing.getAxis().isHorizontal()) {
            facing = Direction.NORTH;
        }

        // depth runs along the facing, with the width across it
        boolean facingOnX = facing.getAxis() == Direction.Axis.X;
        int sizeX = facingOnX ? depth : width;
        int sizeZ = facingOnX ? width : depth;

        // centered on the block, an even size takes the extra block on the positive side
        int minX = center.getX() - (sizeX - 1) / 2;
        int minY = center.getY() - (height - 1) / 2;
        int minZ = center.getZ() - (sizeZ - 1) / 2;

        return new AABB(minX, minY, minZ, minX + sizeX, minY + height, minZ + sizeZ);
    }

    public ItemStacksResourceHandler getItemHandler() {
        return inventory;
    }

    public FilterItemHandler getFilterHandler() {
        return filterHandler;
    }

    public int getOffsetX() {
        return leftRightOffset;
    }

    public void setOffsetX(int offsetXPos) {
        this.leftRightOffset = Mth.clamp(offsetXPos, -MAX_OFFSET, MAX_OFFSET);
        setChanged();
        sync();
    }

    public int getOffsetY() {
        return upDownOffset;
    }

    public void setOffsetY(int offsetYPos) {
        this.upDownOffset = Mth.clamp(offsetYPos, -MAX_OFFSET, MAX_OFFSET);
        setChanged();
        sync();
    }

    public int getOffsetZ() {
        return forwardBackOffset;
    }

    public void setOffsetZ(int offsetZPos) {
        this.forwardBackOffset = Mth.clamp(offsetZPos, -MAX_OFFSET, MAX_OFFSET);
        setChanged();
        sync();
    }

    public int getSizeX() {
        return width;
    }

    public void setSizeX(int xSize) {
        this.width = Mth.clamp(xSize, 1, MAX_SIZE);
        setChanged();
        sync();
    }

    public int getSizeY() {
        return height;
    }

    public void setSizeY(int ySize) {
        this.height = Mth.clamp(ySize, 1, MAX_SIZE);
        setChanged();
        sync();
    }

    public int getSizeZ() {
        return depth;
    }

    public void setSizeZ(int zSize) {
        this.depth = Mth.clamp(zSize, 1, MAX_SIZE);
        setChanged();
        sync();
    }

    private BlockPos getAreaCenter() {
        if (level == null) return worldPosition;

        BlockState state = getBlockState();
        Direction facing = state.getValue(ItemCollectorBlock.FACING);

        if (!facing.getAxis().isHorizontal()) {
            facing = Direction.NORTH;
        }

        Direction leftDir;
        if (facing.getAxis().isHorizontal()) {
            leftDir = facing.getCounterClockWise();
        } else {
            leftDir = Direction.EAST;
        }

        int x = worldPosition.getX() + forwardBackOffset * facing.getStepX() + leftRightOffset * leftDir.getStepX();
        int y = worldPosition.getY() + upDownOffset;
        int z = worldPosition.getZ() + forwardBackOffset * facing.getStepZ() + leftRightOffset * leftDir.getStepZ();

        return new BlockPos(x, y, z);
    }


    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new ItemCollectorMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.item_collector");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        inventory.serialize(output.child("inventory"));
        filterHandler.serialize(output.child("filter"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);
        output.putInt("leftRightOffset", leftRightOffset);
        output.putInt("upDownOffset", upDownOffset);
        output.putInt("forwardBackOffset", forwardBackOffset);
        output.putInt("width", width);
        output.putInt("height", height);
        output.putInt("depth", depth);
        output.putBoolean("whitelist", whitelist);


        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inventory.deserialize(input.childOrEmpty("inventory"));
        filterHandler.deserialize(input.childOrEmpty("filter"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);
        leftRightOffset = Mth.clamp(input.getIntOr("leftRightOffset", 0), -MAX_OFFSET, MAX_OFFSET);
        upDownOffset = Mth.clamp(input.getIntOr("upDownOffset", 0), -MAX_OFFSET, MAX_OFFSET);
        forwardBackOffset = Mth.clamp(input.getIntOr("forwardBackOffset", 0), -MAX_OFFSET, MAX_OFFSET);
        width = Mth.clamp(input.getIntOr("width", 1), 1, MAX_SIZE);
        height = Mth.clamp(input.getIntOr("height", 1), 1, MAX_SIZE);
        depth = Mth.clamp(input.getIntOr("depth", 1), 1, MAX_SIZE);
        whitelist = input.getBooleanOr("whitelist", true);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
    }

    @Override
    public boolean isWhitelist() {
        return whitelist;
    }

    @Override
    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
        setChanged();
        sync();
    }
}
