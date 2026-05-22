package com.benbenlaw.utility.screen.collector;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.screen.util.DurationTooltip;
import com.benbenlaw.core.screen.util.FluidRenderingUtils;
import com.benbenlaw.core.screen.util.button.WhitelistButton;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.FluidCollectorBlockEntity;
import com.benbenlaw.utility.block.entity.FluidPlacerBlockEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

import java.util.List;

public class FluidCollectorScreen extends AbstractContainerScreen<FluidCollectorMenu> {
    private static final Identifier TEXTURE = Utility.identifier("textures/gui/fluid_collector_gui.png");
    private static final Identifier PROGRESS_ARROW = Core.identifier("progress_arrow");

    public FluidCollectorScreen(FluidCollectorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        addRenderableWidget(WhitelistButton.create(x + 151, y + 52, 20, 20, menu.blockEntity));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isCrafting()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 92, y + 34, menu.getScaledProgress() + 1, 16);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        DurationTooltip.renderDurationTooltip(guiGraphics, mouseX, mouseY, x, y, 161, 5, menu.data.get(0), menu.data.get(1));

        //Tank
        FluidRenderingUtils.renderFluid(guiGraphics, menu.blockEntity.getFluidHandler(), FluidCollectorBlockEntity.TANK_SLOT, x, y,
                60, 20, 47, 16, mouseX, mouseY, Component.translatable("tooltip.utility.empty"));

        //Filter Tank
        FluidRenderingUtils.renderFluid(guiGraphics, menu.blockEntity.getFilterFluidHandler(), FluidCollectorBlockEntity.TANK_SLOT, x, y,
            134, 53, 16, 16, mouseX, mouseY, Component.translatable("tooltip.utility.empty_filter"));

    }
}