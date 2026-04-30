package com.benbenlaw.utility.event.client;

import com.benbenlaw.core.util.TooltipUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.item.FluidListComponent;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.FluidStack;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class TooltipEvent {

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.CROOK.get(), "tooltip.utility.crook");
        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.SAPLING_GROWER.get(), "tooltip.utility.sapling_grower");
        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.DEATH_STONE.get(), "tooltip.utility.death_stone");
        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.FLOATER.get(), "tooltip.utility.floater");

        if (stack.has(UtilityDataComponents.FLUIDS.get())) {
            FluidListComponent fluidListComponent = stack.get(UtilityDataComponents.FLUIDS.get());

            if (fluidListComponent == null || fluidListComponent.fluids().isEmpty()) return;

            if (Minecraft.getInstance().hasShiftDown()) {
                event.getToolTip().add(Component.translatable("tooltip.utility.fluids_header")
                        .withStyle(ChatFormatting.BLUE));

                for (FluidStack fluid : fluidListComponent.fluids()) {
                    if (!fluid.isEmpty()) {
                        Component fluidLine = Component.literal(" - ")
                                .append(Component.literal(fluid.getAmount() + "mB "))
                                .append(fluid.getHoverName())
                                .withStyle(ChatFormatting.BLUE);

                        event.getToolTip().add(fluidLine);
                    }
                }
            } else {
                event.getToolTip().add(Component.translatable("tooltip.bblcore.shift")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }

    }
}
