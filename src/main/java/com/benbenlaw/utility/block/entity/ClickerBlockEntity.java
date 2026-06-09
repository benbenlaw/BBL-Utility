package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ClickerBlock;
import com.benbenlaw.utility.screen.clicker.ClickerMenu;
import com.benbenlaw.utility.util.FakePlayerUtil;
import com.benbenlaw.utility.util.UsefulFakePlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClickerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;

    public static int INPUT_SLOT = 0;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 1,
            (i, stack) -> true,
            i -> false
    );

    public ClickerBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.CLICKER_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ClickerBlockEntity.this.progress;
                    case 1 -> ClickerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ClickerBlockEntity.this.progress = value;
                    case 1 -> ClickerBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        if (!level.getBlockState(worldPosition).getValue(ClickerBlock.RUNNING)) {
            progress = 0;
            sync();
            return;
        }

        progress++;

        if (progress < maxProgress) return;

        progress = 0;

        ServerLevel serverLevel = (ServerLevel) level;

        ItemStack stack = inventory.getResource(INPUT_SLOT).toStack();

        if (stack.isEmpty()) return;

        Direction facing = getBlockState().getValue(ClickerBlock.FACING);
        BlockPos targetPos = worldPosition.relative(facing);

        UsefulFakePlayer player = UsefulFakePlayer.createPlayer(
                serverLevel,
                new GameProfile(UUID.randomUUID(), "clicker")
        );

        player.setPos(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        );

        player.setYRot(facing.toYRot());
        player.setXRot(0);

        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        System.out.println("[Clicker] BEFORE: " + player.getMainHandItem());
        InteractionResult result = player.gameMode.useItemOn(
                player,
                serverLevel,
                stack,
                InteractionHand.MAIN_HAND,
                net.minecraft.world.phys.BlockHitResult.miss(
                        Vec3.atCenterOf(targetPos),
                        facing,
                        targetPos
                )
        );

        System.out.println("[Clicker] RESULT: " + result);
        ItemStack finalStack = player.getMainHandItem();
        System.out.println("[Clicker] AFTER: " + finalStack);

        inventory.set(
                INPUT_SLOT,
                ItemResource.of(finalStack),
                finalStack.getCount()
        );

        sync();
    }



    public ItemStacksResourceHandler getItemHandler() {
        return inventory;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new ClickerMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.clicker");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        inventory.serialize(output.child("inventory"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inventory.deserialize(input.childOrEmpty("inventory"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
    }
}
