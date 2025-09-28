package com.benbenlaw.utility.item;

import com.benbenlaw.utility.Utility;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class UtilityCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Utility.MOD_ID);

    public static final Supplier<CreativeModeTab> UTILITY_TAB = CREATIVE_MODE_TABS.register(Utility.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> UtilityItems.CROOK.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup." + Utility.MOD_ID))
            .displayItems(UtilityItems.ITEMS.getEntries()).build());
}
