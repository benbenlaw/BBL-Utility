package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.WhitelistBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.FilterItemHandler;
import com.benbenlaw.core.block.entity.handler.item.OutputItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ItemCollectorBlock;
import com.benbenlaw.utility.screen.collector.ItemCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
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

    private final OutputItemHandler outputHandler = new OutputItemHandler(this,9, i -> {
        for (int slot : OUTPUT_SLOTS) {
            if (i == slot) return true;
        }
        return false;
    });

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

                    for (int i = 0; i < outputHandler.size(); i++) {
                        ItemStack outputSlot = outputHandler.getResource(i).toStack();

                        if (!outputSlot.isEmpty() && !ItemStack.isSameItemSameComponents(outputSlot, stack)) continue;

                        try (Transaction tx = Transaction.open(null)) {
                            long inserted = outputHandler.insertInternalReturn(i, resource, stack.getCount(), tx);
                            if (inserted > 0) {
                                tx.commit();
                                stack.shrink((int) inserted);
                                if (stack.isEmpty()) itemEntity.discard();
                                break;
                            }
                        }
                    }
                    if (!fullyInserted) itemEntity.setItem(stack);
                }
            }
        }
    }

    public AABB createArea() {
        BlockPos startPos = getOffsetStartPos();
        BlockState state = getBlockState();
        Direction facing = state.getValue(ItemCollectorBlock.FACING);

        if (!facing.getAxis().isHorizontal()) {
            facing = Direction.NORTH;
        }

        Direction leftDir;
        if (facing.getAxis().isHorizontal()) {
            leftDir = facing.getClockWise();
        } else {
            leftDir = Direction.WEST;
        }

        // Calculate world-space min/max coordinates
        int minX = startPos.getX();
        int minY = startPos.getY();
        int minZ = startPos.getZ();

        // Left/right expansion
        int maxX = minX + leftDir.getStepX() * (width - 1);
        int maxZ = minZ + leftDir.getStepZ() * (width - 1);

        // Forward/back expansion
        maxX += facing.getStepX() * (depth - 1);
        maxZ += facing.getStepZ() * (depth - 1);

        // Up/down expansion
        int maxY = minY + height - 1;

        // Ensure min/max are correct (for negative step)
        double finalMinX = Math.min(minX, maxX);
        double finalMaxX = Math.max(minX, maxX);
        double finalMinY = minY;
        double finalMaxY = maxY;
        double finalMinZ = Math.min(minZ, maxZ);
        double finalMaxZ = Math.max(minZ, maxZ);

        return new AABB(finalMinX, finalMinY, finalMinZ, finalMaxX + 1.0, finalMaxY + 1.0, finalMaxZ + 1.0);
    }




    public OutputItemHandler getOutputHandler() {
        return outputHandler;
    }

    public ResourceHandler<ItemResource> getItemCapability() {
        return outputHandler;
    }

    public FilterItemHandler getFilterHandler() {
        return filterHandler;
    }

    public int getOffsetX() {
        return leftRightOffset;
    }

    public void setOffsetX(int offsetXPos) {
        this.leftRightOffset = offsetXPos;
        setChanged();
        sync();
    }

    public int getOffsetY() {
        return upDownOffset;
    }

    public void setOffsetY(int offsetYPos) {
        this.upDownOffset = offsetYPos;
        setChanged();
        sync();
    }

    public int getOffsetZ() {
        return forwardBackOffset;
    }

    public void setOffsetZ(int offsetZPos) {
        this.forwardBackOffset = offsetZPos;
        setChanged();
        sync();
    }

    public int getSizeX() {
        return width;
    }

    public void setSizeX(int xSize) {
        this.width = xSize;
        setChanged();
        sync();
    }

    public int getSizeY() {
        return height;
    }

    public void setSizeY(int ySize) {
        this.height = ySize;
        setChanged();
        sync();
    }

    public int getSizeZ() {
        return depth;
    }

    public void setSizeZ(int zSize) {
        this.depth = zSize;
        setChanged();
        sync();
    }

    public void onRightClick() {
        Level level = this.getLevel();
        if (level != null && !level.isClientSide()) {
            showWorkingArea((ServerLevel) level);
        }
    }

    private void showWorkingArea(ServerLevel level) {
        AABB range = createArea();

        int minX = (int) Math.floor(range.minX);
        int minY = (int) Math.floor(range.minY);
        int minZ = (int) Math.floor(range.minZ);
        int maxX = (int) Math.ceil(range.maxX);
        int maxY = (int) Math.ceil(range.maxY);
        int maxZ = (int) Math.ceil(range.maxZ);

        // Draw edges
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                spawnParticle(level, x + 0.5, y + 0.5, minZ + 0.5);
                spawnParticle(level, x + 0.5, y + 0.5, maxZ + 0.5);
            }
        }

        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                spawnParticle(level, minX + 0.5, y + 0.5, z + 0.5);
                spawnParticle(level, maxX + 0.5, y + 0.5, z + 0.5);
            }
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                spawnParticle(level, x + 0.5, minY + 0.5, z + 0.5);
                spawnParticle(level, x + 0.5, maxY + 0.5, z + 0.5);
            }
        }
    }


    private void spawnParticle(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.END_ROD,true, true,
                x - 0.5, y - 0.5, z - 0.5,1,0.0, 0.0, 0.0, 0.0);
    }

    private BlockPos getOffsetStartPos() {
        if (level == null) return worldPosition;

        BlockState state = getBlockState();
        Direction facing = state.getValue(ItemCollectorBlock.FACING);

        if (!facing.getAxis().isHorizontal()) {
            facing = Direction.NORTH;
        }

        Direction leftDir;
        if (facing.getAxis().isHorizontal()) {
            leftDir = facing.getClockWise();
        } else {
            leftDir = Direction.WEST;
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

        outputHandler.serialize(output.child("output"));
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

        outputHandler.deserialize(input.childOrEmpty("output"));
        filterHandler.deserialize(input.childOrEmpty("filter"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);
        leftRightOffset = input.getIntOr("leftRightOffset", 0);
        upDownOffset = input.getIntOr("upDownOffset", 0);
        forwardBackOffset = input.getIntOr("forwardBackOffset", 0);
        width = input.getIntOr("width", 1);
        height = input.getIntOr("height", 1);
        depth = input.getIntOr("depth", 1);
        whitelist = input.getBooleanOr("whitelist", true);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(outputHandler);
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
