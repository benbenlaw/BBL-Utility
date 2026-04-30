package com.benbenlaw.utility.screen.generator;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.screen.util.DurationTooltip;
import com.benbenlaw.core.screen.util.FluidRenderingUtils;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.FluidGeneratorBlockEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FluidGeneratorScreen extends AbstractContainerScreen<FluidGeneratorMenu> {
    private static final Identifier TEXTURE = Utility.identifier("textures/gui/fluid_generator_gui.png");
    private static final Identifier PROGRESS_ARROW = Core.identifier("progress_arrow");

    public FluidGeneratorScreen(FluidGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isCrafting()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 76, y + 34, menu.getScaledProgress() + 1, 16);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        extractTooltip(guiGraphics, mouseX, mouseY);
        DurationTooltip.renderDurationTooltip(guiGraphics, mouseX, mouseY, x, y, 161, 5, menu.data.get(0), menu.data.get(1));
        renderTanks(guiGraphics, x, y, mouseX, mouseY);

    }

    private void renderTanks(GuiGraphicsExtractor GuiGraphicsExtractor, int x, int y, int mouseX, int mouseY) {
        FluidRenderingUtils.renderFluid(GuiGraphicsExtractor, menu.blockEntity.getFluidHandler(), FluidGeneratorBlockEntity.TANK_SLOT, x, y,
                116, 20, 47, 16, mouseX, mouseY, Component.translatable("tooltip.utility.empty"));
    }
}
