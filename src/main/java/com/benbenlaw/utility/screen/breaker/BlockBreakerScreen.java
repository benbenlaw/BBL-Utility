package com.benbenlaw.utility.screen.breaker;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.screen.util.DurationTooltip;
import com.benbenlaw.core.screen.util.button.WhitelistButton;
import com.benbenlaw.utility.Utility;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;


public class BlockBreakerScreen extends AbstractContainerScreen<BlockBreakerMenu> {
    private static final Identifier TEXTURE = Utility.identifier("textures/gui/block_breaker_gui.png");
    private static final Identifier PROGRESS_ARROW = Core.identifier("progress_arrow");

    public BlockBreakerScreen(BlockBreakerMenu menu, Inventory inventory, Component title) {
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
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 92, y + 22, menu.getScaledProgress() + 1, 16);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        DurationTooltip.renderDurationTooltip(guiGraphics, mouseX, mouseY, x, y, 161, 5, menu.data.get(0), menu.data.get(1));
    }
}
