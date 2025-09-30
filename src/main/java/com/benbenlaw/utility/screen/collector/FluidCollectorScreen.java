package com.benbenlaw.utility.screen.collector;

import com.benbenlaw.core.screen.util.FluidRenderingUtils;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.network.packets.SyncWhitelistMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class FluidCollectorScreen extends AbstractContainerScreen<FluidCollectorMenu> {
    private static final ResourceLocation TEXTURE = Utility.rl("textures/gui/fluid_collector_gui.png");
    private static final ResourceLocation PROGRESS_ARROW = Utility.rl("progress_arrow");

    private Button whitelistButton;

    public FluidCollectorScreen(FluidCollectorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        whitelistButton = Button.builder(Component.literal("WHITELIST"), button -> {
            menu.blockEntity.toggleWhitelistMode();
            ClientPacketDistributor.sendToServer(new SyncWhitelistMode(menu.blockEntity.getBlockPos(), menu.blockEntity.getWhitelist()));
            button.setMessage(Component.literal("" + (menu.whitelist ? "WHITELIST" : "BLACKLIST")));
        }).bounds(x + 120, y + 20, 60, 20).build();

        addRenderableWidget(whitelistButton);
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

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTanks(guiGraphics, x, y, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderTanks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        FluidRenderingUtils.renderFluid(guiGraphics, menu.blockEntity.TANK, x, y, 60, 20, 47, 16, mouseX, mouseY);
    }
}