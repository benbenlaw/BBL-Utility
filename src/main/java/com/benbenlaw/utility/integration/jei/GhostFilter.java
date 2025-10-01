package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.core.network.packets.UpdateFilterSlotsPacket;
import com.benbenlaw.core.screen.util.slot.FilterSlot;
import com.benbenlaw.utility.screen.breaker.BlockBreakerScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class GhostFilter implements IGhostIngredientHandler<BlockBreakerScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(BlockBreakerScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();

        for (Slot slot : gui.getMenu().slots) {
            Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);

            if (ingredient.getIngredient() instanceof ItemStack && (slot instanceof FilterSlot)) {
                targets.add(new Target<I>() {
                    @Override
                    public Rect2i getArea() {
                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient) {
                        slot.set((ItemStack) ingredient);
                        ClientPacketDistributor.sendToServer(new UpdateFilterSlotsPacket(slot.index, (ItemStack) ingredient));
                    }
                });
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {

    }
}
