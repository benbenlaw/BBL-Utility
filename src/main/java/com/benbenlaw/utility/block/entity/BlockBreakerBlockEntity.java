package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.InputOutputItemHandler;
import com.benbenlaw.core.util.FakePlayerUtil;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.BlockBreakerBlock;
import com.benbenlaw.utility.block.custom.BlockPlacerBlock;
import com.benbenlaw.utility.screen.breaker.BlockBreakerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockBreakerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private float maxProgress = 1.0f;
    private float progress = 0f;
    private FakePlayer fakePlayer;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            sync();
        }
    };
    public static final int INPUT_SLOT = 0;

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public IItemHandler getIItemHandler(Direction side) {
        return new InputOutputItemHandler(itemHandler,
                (i, stack) -> i == INPUT_SLOT,
                i -> false);
    }

    public BlockBreakerBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.BLOCK_BREAKER_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return (int) switch (index) {
                    case 0 -> BlockBreakerBlockEntity.this.progress * 1000;
                    case 1 -> BlockBreakerBlockEntity.this.maxProgress * 1000;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> BlockBreakerBlockEntity.this.progress = value / 1000f;
                    case 1 -> BlockBreakerBlockEntity.this.maxProgress = value / 1000f;
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

            if (fakePlayer == null) {
                fakePlayer = FakePlayerUtil.createFakePlayer((ServerLevel) level, "Block_Breaker");
            }

            ItemStack tool = itemHandler.getStackInSlot(INPUT_SLOT);
            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, tool);

            if (!level.getBlockState(worldPosition).getValue(BlockBreakerBlock.RUNNING)) return;

            BlockPos targetPos = worldPosition.relative(level.getBlockState(worldPosition).getValue(BlockBreakerBlock.FACING));
            BlockState targetBlockState = level.getBlockState(targetPos);
            BlockEntity targetBlockEntity = level.getBlockEntity(targetPos);

            if (targetBlockState.isAir()) {
                progress = 0f;
                return;
            }

            boolean canHarvest = tool.isCorrectToolForDrops(targetBlockState) || !targetBlockState.requiresCorrectToolForDrops();
            float toolSpeed = tool.getDestroySpeed(targetBlockState);

            HolderLookup.RegistryLookup<Enchantment> registry = Objects.requireNonNull(level.getServer()).registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            int efficiencyLevel = tool.getEnchantmentLevel(registry.getOrThrow(Enchantments.EFFICIENCY));
            if (efficiencyLevel > 0) {
                toolSpeed += (efficiencyLevel * efficiencyLevel + 1);
            }

            float blockHardness = targetBlockState.getDestroySpeed(level, targetPos);
            if (blockHardness < 0f) return;
            float progressPerTick = (toolSpeed / blockHardness) / 30f;

            sendBlockBreakingPacket(this.getPersistentData().getId(), targetPos, progressPerTick);

            if (canHarvest) {
                progress += progressPerTick;

                if (progress >= 1.0f) {
                    Block.dropResources(targetBlockState, level, targetPos, targetBlockEntity, fakePlayer, tool);
                    level.destroyBlock(targetPos, false, fakePlayer);

                    if (tool.isDamageableItem()) {
                        tool.hurtAndBreak(1, fakePlayer, fakePlayer.getEquipmentSlotForItem(tool));
                    }
                    clearBlockBreakingProgress(this.getPersistentData().getId(), targetPos, progressPerTick);
                    progress = 0f;
                }
            } else {
                clearBlockBreakingProgress(this.getPersistentData().getId(), targetPos, progressPerTick);
                progress = 0f;
            }
        }
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
        return new BlockBreakerMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.block_breaker");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        itemHandler.serialize(output);
        output.putFloat("maxProgress", maxProgress);
        output.putFloat("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        itemHandler.deserialize(input);
        maxProgress = input.getFloatOr("maxProgress", 1.0f);
        progress = input.getFloatOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            stacks.add(itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, stacks);
    }
}
