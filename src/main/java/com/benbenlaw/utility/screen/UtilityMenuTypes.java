package com.benbenlaw.utility.screen;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.ResourceGeneratorBlockEntity;
import com.benbenlaw.utility.screen.breaker.BlockBreakerMenu;
import com.benbenlaw.utility.screen.collector.FluidCollectorMenu;
import com.benbenlaw.utility.screen.drying.DryingTableMenu;
import com.benbenlaw.utility.screen.generator.ResourceGeneratorMenu;
import com.benbenlaw.utility.screen.placer.BlockPlacerMenu;
import com.benbenlaw.utility.screen.placer.FluidPlacerMenu;
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

    public static final DeferredHolder<MenuType<?>, MenuType<ResourceGeneratorMenu>> RESOURCE_GENERATOR_MENU = MENUS.register("resource_generator_menu",
            () -> IMenuTypeExtension.create(ResourceGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<FluidPlacerMenu>> FLUID_PLACER_MENU = MENUS.register("fluid_placer_menu",
            () -> IMenuTypeExtension.create(FluidPlacerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<FluidCollectorMenu>> FLUID_COLLECTOR_MENU = MENUS.register("fluid_collector_menu",
            () -> IMenuTypeExtension.create(FluidCollectorMenu::new));

}

