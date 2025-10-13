package com.benbenlaw.utility.screen.collector;

import com.benbenlaw.core.screen.util.button.WhitelistButton;
import com.benbenlaw.utility.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collections;
import java.util.List;


public class ItemCollectorScreen extends AbstractContainerScreen<ItemCollectorMenu> {
    private static final ResourceLocation TEXTURE = Utility.rl("textures/gui/item_collector_gui.png");
    private static final ResourceLocation PROGRESS_ARROW = Utility.rl("progress_arrow");

    private EditBox offsetXPos;
    private EditBox offsetYPos;
    private EditBox offsetZPos;

    private EditBox xSize;
    private EditBox ySize;
    private EditBox zSize;

    public ItemCollectorScreen(ItemCollectorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        this.offsetXPos = new EditBox(this.font, this.leftPos + 9, this.topPos + 38, 14, 12, Component.literal("Offset X"));
        this.offsetXPos.setMaxLength(1);
        this.offsetXPos.setValue(String.valueOf(menu.blockEntity.getOffsetX()));

        this.offsetYPos = new EditBox(this.font, this.leftPos + 27, this.topPos + 38, 14, 12, Component.literal("Offset Y"));
        this.offsetYPos.setMaxLength(1);
        this.offsetYPos.setValue(String.valueOf(menu.blockEntity.getOffsetY()));

        this.offsetZPos = new EditBox(this.font, this.leftPos + 45, this.topPos + 38, 14, 12, Component.literal("Offset Z"));
        this.offsetZPos.setMaxLength(1);
        this.offsetZPos.setValue(String.valueOf(menu.blockEntity.getOffsetZ()));

        this.xSize = new EditBox(this.font, this.leftPos + 63, this.topPos + 38, 14, 12, Component.literal("X Size"));
        this.xSize.setMaxLength(1);
        this.xSize.setValue(String.valueOf(menu.blockEntity.getSizeX()));

        this.ySize = new EditBox(this.font, this.leftPos + 81, this.topPos + 38, 14, 12, Component.literal("Y Size"));
        this.ySize.setMaxLength(1);
        this.ySize.setValue(String.valueOf(menu.blockEntity.getSizeY()));

        this.zSize = new EditBox(this.font, this.leftPos + 99, this.topPos + 38, 14, 12, Component.literal("Z Size"));
        this.zSize.setMaxLength(1);
        this.zSize.setValue(String.valueOf(menu.blockEntity.getSizeZ()));

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
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 92, y + 22, menu.getScaledProgress() + 1, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (this.offsetXPos != null) this.offsetXPos.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.offsetYPos != null) this.offsetYPos.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.offsetZPos != null) this.offsetZPos.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.xSize != null) this.xSize.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.ySize != null) this.ySize.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.zSize != null) this.zSize.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.offsetXPos != null && this.offsetXPos.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.offset_x", mouseX, mouseY);
        } else if (this.offsetYPos != null && this.offsetYPos.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.offset_y", mouseX, mouseY);
        } else if (this.offsetZPos != null && this.offsetZPos.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.offset_z", mouseX, mouseY);
        } else if (this.xSize != null && this.xSize.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.size_x", mouseX, mouseY);
        } else if (this.ySize != null && this.ySize.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.size_y", mouseX, mouseY);
        } else if (this.zSize != null && this.zSize.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.size_z", mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

    private void renderTooltip(GuiGraphics guiGraphics, String text, int mouseX, int mouseY) {
        List<ClientTooltipComponent> components =
                Collections.singletonList(ClientTooltipComponent.create(Component.translatable(text).getVisualOrderText()));
        guiGraphics.renderTooltip(Minecraft.getInstance().font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }
}
