package com.benbenlaw.utility.screen.clicker;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.screen.util.DurationTooltip;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.network.packets.SyncClickerSettings;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ClickerScreen extends AbstractContainerScreen<ClickerMenu> {
    private static final Identifier TEXTURE = Utility.identifier("textures/gui/clicker_gui.png");
    private static final Identifier PROGRESS_ARROW = Core.identifier("progress_arrow");

    private Button clickTypeButton;
    private Button sneakButton;

    public ClickerScreen(ClickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        clickTypeButton = addRenderableWidget(Button.builder(Component.empty(),
                        button -> ClientPacketDistributor.sendToServer(new SyncClickerSettings(menu.blockPos, false)))
                .bounds(x + 104, y + 22, 64, 20).build());

        sneakButton = addRenderableWidget(Button.builder(Component.empty(),
                        button -> ClientPacketDistributor.sendToServer(new SyncClickerSettings(menu.blockPos, true)))
                .bounds(x + 104, y + 46, 64, 20).build());
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
        clickTypeButton.setMessage(Component.translatable(menu.isLeftClick() ? "gui.utility.clicker.left_click" : "gui.utility.clicker.right_click"));
        sneakButton.setMessage(Component.translatable(menu.isSneaking() ? "gui.utility.clicker.sneak_on" : "gui.utility.clicker.sneak_off"));

        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        DurationTooltip.renderDurationTooltip(guiGraphics, mouseX, mouseY, x, y, 161, 5, menu.data.get(0), menu.data.get(1));
    }
}
