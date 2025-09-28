package com.benbenlaw.utility.screen.placer;

import com.benbenlaw.utility.block.entity.BlockPlacerBlockEntity;
import com.benbenlaw.utility.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.utility.screen.UtilityMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BlockPlacerMenu extends SimpleAbstractContainerMenu {

    protected BlockPlacerBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;

    public BlockPlacerMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));
    }

    public BlockPlacerMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(UtilityMenuTypes.BLOCK_PLACER_MENU.get(), containerID, inventory, pos, 1);
        this.player = inventory.player;
        this.level = inventory.player.level();
        this.data = data;
        this.blockPos = pos;
        this.blockEntity = (BlockPlacerBlockEntity) level.getBlockEntity(pos);

        assert blockEntity != null;
        this.addSlot(new SlotItemHandler(blockEntity.getItemStackHandler(), BlockPlacerBlockEntity.INPUT_SLOT, 60, 35));

        this.addDataSlots(data);
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
