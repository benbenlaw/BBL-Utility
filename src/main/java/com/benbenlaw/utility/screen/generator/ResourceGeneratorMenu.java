package com.benbenlaw.utility.screen.generator;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.core.screen.util.slot.ResultSlot;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import com.benbenlaw.utility.screen.UtilityMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;

public class ResourceGeneratorMenu extends SimpleAbstractContainerMenu {

    protected ResourceGeneratorBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;

    public ResourceGeneratorMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));
    }

    public ResourceGeneratorMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(UtilityMenuTypes.RESOURCE_GENERATOR_MENU.get(), containerID, inventory, pos, 2);
        this.player = inventory.player;
        this.level = inventory.player.level();
        this.data = data;
        this.blockPos = pos;
        this.blockEntity = (ResourceGeneratorBlockEntity) level.getBlockEntity(pos);

        assert blockEntity != null;
        this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set,
                ResourceGeneratorBlockEntity.INPUT_SLOT, 44, 35).size(1));
        this.addSlot(new ResultSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set,
                ResourceGeneratorBlockEntity.OUTPUT_SLOT, 116, 35));

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
