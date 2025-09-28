package com.benbenlaw.utility.screen;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.screen.breaker.BlockBreakerMenu;
import com.benbenlaw.utility.screen.drying.DryingTableMenu;
import com.benbenlaw.utility.screen.placer.BlockPlacerMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UtilityMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, Utility.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<DryingTableMenu>> DRYING_TABLE_MENU = MENUS.register("drying_table_menu",
            () -> IMenuTypeExtension.create(DryingTableMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BlockPlacerMenu>> BLOCK_PLACER_MENU = MENUS.register("block_placer_menu",
            () -> IMenuTypeExtension.create(BlockPlacerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BlockBreakerMenu>> BLOCK_BREAKER_MENU = MENUS.register("block_breaker_menu",
            () -> IMenuTypeExtension.create(BlockBreakerMenu::new));
}

