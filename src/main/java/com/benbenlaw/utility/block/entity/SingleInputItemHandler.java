package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.BiPredicate;

public class SingleInputItemHandler extends InputItemHandler {

    public SingleInputItemHandler(SyncableBlockEntity blockEntity, int size, BiPredicate<Integer, ItemStack> canInsert) {
        super(blockEntity, size, canInsert);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty()) {
            return 0;
        }

        // Only allow one item per slot
        ItemStack currentStack = getResource(index).toStack();
        if (!currentStack.isEmpty()) {
            return 0; // Slot already has an item
        }

        // Force amount to 1
        ItemStack stack = resource.toStack(1);

        // Can't access private canInsert, so just call super.insert (which uses canInsert internally)
        return super.insert(index, resource, 1, transaction);
    }
}