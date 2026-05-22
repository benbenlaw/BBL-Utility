package com.benbenlaw.utility.screen.compactor;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.core.screen.util.slot.ResultSlot;
import com.benbenlaw.utility.block.entity.CompactorBlockEntity;
import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.screen.UtilityMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;

public class CompactorMenu extends SimpleAbstractContainerMenu {

    protected CompactorBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;

    public CompactorMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));
    }

    public CompactorMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(UtilityMenuTypes.COMPACTOR_MENU.get(), containerID, inventory, pos, 18);
        this.player = inventory.player;
        this.level = inventory.player.level();
        this.data = data;
        this.blockPos = pos;
        this.blockEntity = (CompactorBlockEntity) level.getBlockEntity(pos);

        assert blockEntity != null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int slot = i * 3 + j;
                this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, slot, 14 + j * 18, 17 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int slot = i * 3 + j + 9;
                this.addSlot(new ResultSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, slot, 110 + j * 18, 17 + i * 18));
            }
        }

        this.addDataSlots(data);
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
