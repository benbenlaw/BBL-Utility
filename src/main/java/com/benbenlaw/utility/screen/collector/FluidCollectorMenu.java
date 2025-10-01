package com.benbenlaw.utility.screen.collector;

import com.benbenlaw.core.screen.util.slot.FilterSlot;
import com.benbenlaw.utility.block.entity.FluidCollectorBlockEntity;
import com.benbenlaw.utility.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.utility.screen.UtilityMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FluidCollectorMenu extends SimpleAbstractContainerMenu {

    protected FluidCollectorBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;

    public FluidCollectorMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));
    }

    public FluidCollectorMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(UtilityMenuTypes.FLUID_COLLECTOR_MENU.get(), containerID, inventory, pos, 0);
        this.player = inventory.player;
        this.level = inventory.player.level();
        this.data = data;
        this.blockPos = pos;
        this.blockEntity = (FluidCollectorBlockEntity) level.getBlockEntity(pos);
        assert blockEntity != null;

        this.addSlot(new FilterSlot(blockEntity.getFilterItemHandler(), 0, 134, 53));

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
        return data.get(0) > 0 ;
    }

    public int getScaledProgress() {

        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // This is the height/width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
}
