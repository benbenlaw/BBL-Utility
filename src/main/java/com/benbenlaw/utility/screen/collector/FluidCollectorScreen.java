package com.benbenlaw.utility.screen.collector;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.screen.util.FluidRenderingUtils;
import com.benbenlaw.core.screen.util.button.WhitelistButton;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.FluidCollectorBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

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
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isCrafting()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 92, y + 34, menu.getScaledProgress() + 1, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        FluidStack fluidStack = FluidUtil.getStack(menu.blockEntity.getFilterFluidHandler(), FluidCollectorBlockEntity.TANK_SLOT);

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        FluidRenderingUtils.renderFluidStack(guiGraphics, fluidStack,
                x + 134, y + 53, 16, 16, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTanks(guiGraphics, x, y, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
        FluidRenderingUtils.renderFluidStackTooltip(guiGraphics, fluidStack, x + 134, y + 53, 16, 16, mouseX, mouseY );
    }

    private void renderTanks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        FluidRenderingUtils.renderFluid(guiGraphics, menu.blockEntity.getFluidOutputHandler(), FluidCollectorBlockEntity.TANK_SLOT, x, y, 60, 20, 47, 16, mouseX, mouseY);
    }
}