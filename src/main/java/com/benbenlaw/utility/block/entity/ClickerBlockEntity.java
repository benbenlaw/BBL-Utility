package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.ClickerBlock;
import com.benbenlaw.utility.screen.clicker.ClickerMenu;
import com.benbenlaw.core.util.FakePlayerUtil;
import com.benbenlaw.utility.util.ClickerUtil;
import com.benbenlaw.utility.util.UtilityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClickerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 20;
    private int progress = 0;

    public static int INPUT_SLOT = 0;

    private boolean leftClick = false;
    private boolean sneaking = false;
    private FakePlayer fakePlayer;
    private long lastAttackTime = 0;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 1,
            (i, stack) -> !stack.is(UtilityTags.Items.CLICKER_BLACKLIST),
            i -> true
    );

    public ClickerBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.CLICKER_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ClickerBlockEntity.this.progress;
                    case 1 -> ClickerBlockEntity.this.maxProgress;
                    case 2 -> ClickerBlockEntity.this.leftClick ? 1 : 0;
                    case 3 -> ClickerBlockEntity.this.sneaking ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ClickerBlockEntity.this.progress = value;
                    case 1 -> ClickerBlockEntity.this.maxProgress = value;
                    case 2 -> ClickerBlockEntity.this.leftClick = value != 0;
                    case 3 -> ClickerBlockEntity.this.sneaking = value != 0;
                }
            }

            @Override
            public int getCount() {
                return 4;
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

        ServerLevel serverLevel = (ServerLevel) level;

        ItemStack stack = inventory.getResource(INPUT_SLOT).toStack(inventory.getAmountAsInt(INPUT_SLOT));

        if (leftClick && serverLevel.getGameTime() - lastAttackTime < ClickerUtil.attackDelay(stack)) {
            progress = maxProgress;
            return;
        }

        progress = 0;

        Direction facing = getBlockState().getValue(ClickerBlock.FACING);

        if (fakePlayer == null) {
            fakePlayer = FakePlayerUtil.createFakePlayer(serverLevel, "Clicker");
            ClickerUtil.prepare(fakePlayer);
        }
        FakePlayer player = fakePlayer;

        Vec3 eyePos = Vec3.atCenterOf(worldPosition).relative(facing, 0.51);
        ClickerUtil.setup(player, eyePos, facing, stack, sneaking);

        if (leftClick) {
            if (ClickerUtil.leftClick(player, serverLevel)) {
                lastAttackTime = serverLevel.getGameTime();
            }
        } else {
            ClickerUtil.rightClick(player, serverLevel);
        }

        ItemStack finalStack = player.getMainHandItem().copy();
        ClickerUtil.cleanup(player, stack);

        if (finalStack.isEmpty()) {
            if (stack.isDamageableItem()) {
                serverLevel.playSound(null, worldPosition, SoundEvents.ITEM_BREAK.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            inventory.set(INPUT_SLOT, ItemResource.EMPTY, 0);
        } else {
            inventory.set(INPUT_SLOT, ItemResource.of(finalStack), finalStack.getCount());
        }

        sync();
    }

    public boolean isLeftClick() {
        return leftClick;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void toggleClickType() {
        leftClick = !leftClick;
        sync();
    }

    public void toggleSneaking() {
        sneaking = !sneaking;
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
        output.putBoolean("leftClick", leftClick);
        output.putBoolean("sneaking", sneaking);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inventory.deserialize(input.childOrEmpty("inventory"));
        maxProgress = input.getIntOr("maxProgress", 20);
        progress = input.getIntOr("progress", 0);
        leftClick = input.getBooleanOr("leftClick", false);
        sneaking = input.getBooleanOr("sneaking", false);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
    }
}
