package com.benbenlaw.utility.screen.generator;

import com.benbenlaw.core.screen.util.FluidRenderingUtils;
import com.benbenlaw.utility.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class ResourceGeneratorScreen extends AbstractContainerScreen<ResourceGeneratorMenu> {
    private static final ResourceLocation TEXTURE = Utility.rl("textures/gui/resource_generator_gui.png");
    private static final ResourceLocation PROGRESS_ARROW = Utility.rl("progress_arrow");

    public ResourceGeneratorScreen(ResourceGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isCrafting()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 76, y + 34, menu.getScaledProgress() + 1, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTanks(guiGraphics, x, y, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderTanks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        FluidRenderingUtils.renderFluid(guiGraphics, menu.blockEntity.LEFT_TANK, x, y, 8, 20, 47, 16, mouseX, mouseY);
        FluidRenderingUtils.renderFluid(guiGraphics, menu.blockEntity.RIGHT_TANK, x, y, 152, 20, 47, 16, mouseX, mouseY);
    }
}
