package com.benbenlaw.utility.screen.drying;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.custom.DryingTableBlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DryingTableScreen extends AbstractContainerScreen<DryingTableMenu> {
    private static final ResourceLocation TEXTURE = Utility.rl("textures/gui/drying_table_gui.png");
    private static final ResourceLocation PROGRESS_ARROW = Utility.rl("progress_arrow");
    private static final ResourceLocation WATERLOGGED_PROGRESS_ARROW = Utility.rl("waterlogged_progress_arrow");

    public DryingTableScreen(DryingTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isCrafting()) {
            if (menu.blockEntity.getBlockState().getValue(DryingTableBlock.WATERLOGGED)) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, WATERLOGGED_PROGRESS_ARROW, 24, 16, 0, 0, x + 76, y + 34, menu.getScaledProgress() + 1, 16);
            } else {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 76, y + 34, menu.getScaledProgress() + 1, 16);
            }
        }

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
