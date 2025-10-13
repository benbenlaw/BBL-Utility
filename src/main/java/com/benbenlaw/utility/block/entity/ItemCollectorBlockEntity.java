package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.WhitelistBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.FilterItemHandler;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.core.block.entity.handler.item.OutputItemHandler;
import com.benbenlaw.core.util.FakePlayerUtil;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ItemCollectorBlock;
import com.benbenlaw.utility.screen.collector.ItemCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemCollectorBlockEntity extends SyncableBlockEntity implements MenuProvider, WhitelistBlockEntity {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;

    private int offsetXPos = 0;
    private int offsetYPos = 0;
    private int offsetZPos = 0;

    private int xSize = 1;
    private int ySize = 1;
    private int zSize = 1;

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
                System.out.println(box);

                for (Entity entity : entityList) {
                    if (entity instanceof ItemEntity itemEntity) {
                        ItemStack stack = itemEntity.getItem();
                        if (stack.isEmpty()) continue;

                        System.out.println(entity);

                        boolean canInsert = filterHandler.matchesItem(stack, whitelist);

                        if (canInsert) {
                            int slot = getInsertableSlot(stack);
                            if (slot != -1) {
                                ItemResource resource = ItemResource.of(stack);
                                try (Transaction tx = Transaction.open(null)) {
                                    outputHandler.insertInternal(slot, resource, stack.getCount(), tx);
                                    tx.commit();
                                }
                                itemEntity.discard();
                            }
                        }

                    }
                }

            }
        }
    }

    private int getInsertableSlot(ItemStack stack) {
        for (int i = 0; i < outputHandler.size(); i++) {
            ItemStack outputSlot = outputHandler.getResource(i).toStack();
            if (outputSlot.isEmpty()) {
                return i; // empty slot
            } else if (ItemStack.isSameItemSameComponents(outputSlot, stack)) {
                int result = outputSlot.getCount() + stack.getCount();
                if (result <= outputSlot.getMaxStackSize()) {
                    return i; // same item and enough space
                }
            }
        }
        return -1; // no valid slot
    }

    public AABB createArea() {
        if (level == null) return null;

        BlockState state = getBlockState();
        Direction facing = state.getValue(ItemCollectorBlock.FACING);

        int xAmount = Math.max(1, xSize);
        int yAmount = Math.max(1, ySize);
        int zAmount = Math.max(1, zSize);

        BlockPos startPos = worldPosition.relative(facing, 1);

        startPos = startPos.offset(offsetXPos, offsetYPos, offsetZPos);

        int minX = startPos.getX();
        int minY = startPos.getY();
        int minZ = startPos.getZ();
        int maxX = minX + xAmount - 1;
        int maxY = minY + yAmount - 1;
        int maxZ = minZ + zAmount - 1;

        return new AABB(minX, minY, minZ, maxX + 1.0, maxY + 1.0, maxZ + 1.0); // +1 because AABB max is exclusive
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
        return offsetXPos;
    }

    public void setOffsetX(int offsetXPos) {
        this.offsetXPos = offsetXPos;
        setChanged();
        sync();
    }

    public int getOffsetY() {
        return offsetYPos;
    }

    public void setOffsetY(int offsetYPos) {
        this.offsetYPos = offsetYPos;
        setChanged();
        sync();
    }

    public int getOffsetZ() {
        return offsetZPos;
    }

    public void setOffsetZ(int offsetZPos) {
        this.offsetZPos = offsetZPos;
        setChanged();
        sync();
    }

    public int getSizeX() {
        return xSize;
    }

    public void setSizeX(int xSize) {
        this.xSize = xSize;
        setChanged();
        sync();
    }

    public int getSizeY() {
        return ySize;
    }

    public void setSizeY(int ySize) {
        this.ySize = ySize;
        setChanged();
        sync();
    }

    public int getSizeZ() {
        return zSize;
    }

    public void setSizeZ(int zSize) {
        this.zSize = zSize;
        setChanged();
        sync();
    }

    public void sendBlockBreakingPacket(int breakerId, BlockPos targetPos, float progressPerTick) {
        assert level != null;
        int newCrackStage = Math.min(9, (int) ((progress + progressPerTick) * 10));
        int currentCrackStage = Math.min(9, (int) (progress * 10));

        if (newCrackStage != currentCrackStage || newCrackStage == 0) {
            ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(breakerId, targetPos, newCrackStage);
            for (ServerPlayer player : ((ServerLevel) level).getPlayers(p -> p.distanceToSqr(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < 1024)) {
                player.connection.send(packet);
            }
        }
    }

    public void clearBlockBreakingProgress(int breakerId, BlockPos targetPos, float progressPerTick) {
        if (level instanceof ServerLevel serverLevel) {
            ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(breakerId, targetPos, -1);
            for (ServerPlayer player : serverLevel.getPlayers(p -> p.distanceToSqr(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < 1024)) {
                player.connection.send(packet);
            }
        }
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

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        outputHandler.deserialize(input.childOrEmpty("output"));
        filterHandler.deserialize(input.childOrEmpty("filter"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);

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
