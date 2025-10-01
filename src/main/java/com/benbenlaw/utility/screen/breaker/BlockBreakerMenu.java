package com.benbenlaw.utility.screen.breaker;

import com.benbenlaw.core.screen.util.slot.FilterSlot;
import com.benbenlaw.utility.block.entity.BlockBreakerBlockEntity;
import com.benbenlaw.utility.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.utility.screen.UtilityMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BlockBreakerMenu extends SimpleAbstractContainerMenu {

    protected BlockBreakerBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;

    public BlockBreakerMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));
    }

    public BlockBreakerMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(UtilityMenuTypes.BLOCK_BREAKER_MENU.get(), containerID, inventory, pos, 1);
        this.player = inventory.player;
        this.level = inventory.player.level();
        this.data = data;
        this.blockPos = pos;
        this.blockEntity = (BlockBreakerBlockEntity) level.getBlockEntity(pos);

        assert blockEntity != null;
        this.addSlot(new SlotItemHandler(blockEntity.getItemStackHandler(), BlockBreakerBlockEntity.INPUT_SLOT, 60, 23));

        for (int i = 0; i < 8; i++) {
            this.addSlot(new FilterSlot(blockEntity.getFilterItemHandler(), i, 8 + i * 18, 53));
        }

        this.addDataSlots(data);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < slots.size()) {
            if (this.slots.get(slotId) instanceof FilterSlot filterSlot) {
                ItemStack carried = this.getCarried();
                if (!carried.isEmpty()) {
                    filterSlot.set(carried.copyWithCount(1));
                } else {
                    filterSlot.set(ItemStack.EMPTY);
                }
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }


    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {

        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // This is the height/width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
}
