package com.benbenlaw.utility.event;

import com.benbenlaw.core.util.TooltipUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class TooltipEvent {

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.CROOK.get(), "tooltip.utility.crook");
        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.SAPLING_GROWER.get(), "tooltip.utility.sapling_grower");
        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.DEATH_STONE.get(), "tooltip.utility.death_stone");
        TooltipUtil.addShiftTooltip(stack, event, UtilityItems.FLOATER.get(), "tooltip.utility.floater");


    }
}
