package com.benbenlaw.utility;

import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.block.UtilityCapabilities;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.item.UtilityCreativeTab;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.item.UtilityItems;
import com.benbenlaw.utility.network.UtilityNetworking;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.screen.UtilityMenuTypes;
import com.benbenlaw.utility.screen.breaker.BlockBreakerScreen;
import com.benbenlaw.utility.screen.clock.RedstoneClockScreen;
import com.benbenlaw.utility.screen.collector.FluidCollectorScreen;
import com.benbenlaw.utility.screen.drying.DryingTableScreen;
import com.benbenlaw.utility.screen.generator.FluidGeneratorMenu;
import com.benbenlaw.utility.screen.generator.FluidGeneratorScreen;
import com.benbenlaw.utility.screen.generator.ResourceGeneratorScreen;
import com.benbenlaw.utility.screen.placer.BlockPlacerMenu;
import com.benbenlaw.utility.screen.placer.BlockPlacerScreen;
import com.benbenlaw.utility.screen.placer.FluidPlacerScreen;
import com.benbenlaw.utility.screen.repairer.ItemRepairerScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Utility.MOD_ID)
public class Utility {
    public static final String MOD_ID = "utility";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Utility(final IEventBus eventBus, final ModContainer modContainer) {

        UtilityBlocks.BLOCKS.register(eventBus);
        UtilityBlockEntities.BLOCK_ENTITIES.register(eventBus);
        UtilityItems.ITEMS.register(eventBus);
        UtilityDataComponents.COMPONENTS.register(eventBus);
        UtilityCreativeTab.CREATIVE_MODE_TABS.register(eventBus);
        UtilityMenuTypes.MENUS.register(eventBus);
        UtilityRecipeTypes.SERIALIZER.register(eventBus);
        UtilityRecipeTypes.TYPES.register(eventBus);

        modContainer.registerConfig(ModConfig.Type.STARTUP, UtilityStartUpConfig.SPEC, "bbl/utility/startup.toml");

        eventBus.addListener(this::registerCapabilities);
        eventBus.addListener(this::registerNetworking);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @EventBusSubscriber(modid = Utility.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(UtilityMenuTypes.DRYING_TABLE_MENU.get(), DryingTableScreen::new);
            event.register(UtilityMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            event.register(UtilityMenuTypes.BLOCK_BREAKER_MENU.get(), BlockBreakerScreen::new);
            event.register(UtilityMenuTypes.RESOURCE_GENERATOR_MENU.get(), ResourceGeneratorScreen::new);
            event.register(UtilityMenuTypes.FLUID_PLACER_MENU.get(), FluidPlacerScreen::new);
            event.register(UtilityMenuTypes.FLUID_COLLECTOR_MENU.get(), FluidCollectorScreen::new);
            event.register(UtilityMenuTypes.ITEM_REPAIRER_MENU.get(), ItemRepairerScreen::new);
            event.register(UtilityMenuTypes.FLUID_GENERATOR_MENU.get(), FluidGeneratorScreen::new);
            event.register(UtilityMenuTypes.REDSTONE_CLOCK_MENU.get(), RedstoneClockScreen::new);
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        UtilityCapabilities.registerCapabilities(event);
    }

    public void registerNetworking(RegisterPayloadHandlersEvent event) {
        UtilityNetworking.registerNetworking(event);
    }

}
