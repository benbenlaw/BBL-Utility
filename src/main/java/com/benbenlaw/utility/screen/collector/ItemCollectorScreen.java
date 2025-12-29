package com.benbenlaw.utility.screen.collector;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.screen.util.DurationTooltip;
import com.benbenlaw.core.screen.util.button.WhitelistButton;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.network.packets.SyncItemCollectorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Collections;
import java.util.List;


public class ItemCollectorScreen extends AbstractContainerScreen<ItemCollectorMenu> {
    private static final Identifier TEXTURE = Utility.identifier("textures/gui/item_collector_gui.png");
    private static final Identifier PROGRESS_ARROW = Core.identifier("progress_arrow");

    private EditBox leftRightOffset;
    private EditBox upDownOffset;
    private EditBox forwardBackOffset;

    private EditBox widthSize;
    private EditBox heightSize;
    private EditBox depthSize;

    public ItemCollectorScreen(ItemCollectorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        this.leftRightOffset = new EditBox(this.font, this.leftPos + 9, this.topPos + 38, 21, 12, Component.literal("Left/Right Offset"));
        this.leftRightOffset.setMaxLength(2);
        this.leftRightOffset.setValue(String.valueOf(menu.blockEntity.getOffsetX()));

        this.upDownOffset = new EditBox(this.font, this.leftPos + 34, this.topPos + 38, 21, 12, Component.literal("Up/Down Offset"));
        this.upDownOffset.setMaxLength(2);
        this.upDownOffset.setValue(String.valueOf(menu.blockEntity.getOffsetY()));

        this.forwardBackOffset = new EditBox(this.font, this.leftPos + 59, this.topPos + 38, 21, 12, Component.literal("Forward/Back Offset"));
        this.forwardBackOffset.setMaxLength(2);
        this.forwardBackOffset.setValue(String.valueOf(menu.blockEntity.getOffsetZ()));

        this.widthSize = new EditBox(this.font, this.leftPos + 84, this.topPos + 38, 14, 12, Component.literal("Width"));
        this.widthSize.setMaxLength(1);
        this.widthSize.setValue(String.valueOf(menu.blockEntity.getSizeX()));

        this.heightSize = new EditBox(this.font, this.leftPos + 102, this.topPos + 38, 14, 12, Component.literal("Height"));
        this.heightSize.setMaxLength(1);
        this.heightSize.setValue(String.valueOf(menu.blockEntity.getSizeY()));

        this.depthSize = new EditBox(this.font, this.leftPos + 120, this.topPos + 38, 14, 12, Component.literal("Depth"));
        this.depthSize.setMaxLength(1);
        this.depthSize.setValue(String.valueOf(menu.blockEntity.getSizeZ()));

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.leftRightOffset.setResponder(this::onSearchChanged);
        this.upDownOffset.setResponder(this::onSearchChanged);
        this.forwardBackOffset.setResponder(this::onSearchChanged);
        this.widthSize.setResponder(this::onSearchChanged);
        this.heightSize.setResponder(this::onSearchChanged);
        this.depthSize.setResponder(this::onSearchChanged);

        addRenderableWidget(WhitelistButton.create(x + 151, y + 52, 20, 20, menu.blockEntity));

    }

    private void onSearchChanged(String text) {
        int offsetXPosValue = parseSafe(leftRightOffset);
        int offsetYPosValue = parseSafe(upDownOffset);
        int offsetZPosValue = parseSafe(forwardBackOffset);
        int xSizeValue = Math.max(1, parseSafe(widthSize));
        int ySizeValue = Math.max(1, parseSafe(heightSize));
        int zSizeValue = Math.max(1, parseSafe(depthSize));

        ClientPacketDistributor.sendToServer(new SyncItemCollectorPacket(menu.blockPos, offsetXPosValue, offsetYPosValue, offsetZPosValue,
                xSizeValue, ySizeValue, zSizeValue));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        /** Render Progress Arrow - correctly disabled as not sure its really needed*/

        //if (menu.isCrafting()) {
        //    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 140, y + 35, menu.getScaledProgress() + 1, 16);
        //}
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderTooltip(guiGraphics, mouseX, mouseY);

        if (this.leftRightOffset != null) this.leftRightOffset.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.upDownOffset != null) this.upDownOffset.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.forwardBackOffset != null) this.forwardBackOffset.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.widthSize != null) this.widthSize.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.heightSize != null) this.heightSize.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.depthSize != null) this.depthSize.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.leftRightOffset != null && this.leftRightOffset.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.left_right", mouseX, mouseY);
        } else if (this.upDownOffset != null && this.upDownOffset.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.up_down", mouseX, mouseY);
        } else if (this.forwardBackOffset != null && this.forwardBackOffset.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.forward_backward", mouseX, mouseY);
        } else if (this.widthSize != null && this.widthSize.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.width", mouseX, mouseY);
        } else if (this.heightSize != null && this.heightSize.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.height", mouseX, mouseY);
        } else if (this.depthSize != null && this.depthSize.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.item_collector.depth", mouseX, mouseY);
        }

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        DurationTooltip.renderDurationTooltip(guiGraphics, mouseX, mouseY, x, y, 161, 5, menu.data.get(0), menu.data.get(1));
    }

    private void renderTooltip(GuiGraphics guiGraphics, String text, int mouseX, int mouseY) {
        List<ClientTooltipComponent> components =
                Collections.singletonList(ClientTooltipComponent.create(Component.translatable(text).getVisualOrderText()));
        guiGraphics.renderTooltip(Minecraft.getInstance().font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.leftRightOffset.charTyped(event)) {
            return true;
        }
        if (this.upDownOffset.charTyped(event)) {
            return true;
        }
        if (this.forwardBackOffset.charTyped(event)) {
            return true;
        }
        if (this.widthSize.charTyped(event)) {
            return true;
        }
        if (this.heightSize.charTyped(event)) {
            return true;
        }
        if (this.depthSize.charTyped(event)) {
            return true;
        }


        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {

        if (this.leftRightOffset.keyPressed(event)) {
            return true;
        }
        if (this.upDownOffset.keyPressed(event)) {
            return true;
        }
        if (this.forwardBackOffset.keyPressed(event)) {
            return true;
        }
        if (this.depthSize.keyPressed(event)) {
            return true;
        }
        if (this.heightSize.keyPressed(event)) {
            return true;
        }
        if (this.depthSize.keyPressed(event)) {
            return true;
        }


        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {

        if (this.leftRightOffset.mouseClicked(event, doubleClick)) {
            this.setFocused(this.leftRightOffset);
            return true;
        }
        if (this.upDownOffset.mouseClicked(event, doubleClick)) {
            this.setFocused(this.upDownOffset);
            return true;
        }
        if (this.forwardBackOffset.mouseClicked(event, doubleClick)) {
            this.setFocused(this.forwardBackOffset);
            return true;
        }
        if (this.widthSize.mouseClicked(event, doubleClick)) {
            this.setFocused(this.widthSize);
            return true;
        }
        if (this.heightSize.mouseClicked(event, doubleClick)) {
            this.setFocused(this.heightSize);
            return true;
        }
        if (this.depthSize.mouseClicked(event, doubleClick)) {
            this.setFocused(this.depthSize);
            return true;
        }

        return super.mouseClicked(event, doubleClick);

    }

    private int parseSafe(EditBox box) {
        String value = box.getValue();
        if (value == null || value.isEmpty()) return 0;
        try {
            int parsed = Integer.parseInt(value);
            // Clamp between -9 and 9
            return Math.max(-9, Math.min(9, parsed));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
