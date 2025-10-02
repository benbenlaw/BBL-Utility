package com.benbenlaw.utility.screen.clock;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.network.packets.SyncRedstoneClockPacket;
import com.benbenlaw.utility.screen.repairer.ItemRepairerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedstoneClockScreen extends AbstractContainerScreen<RedstoneClockMenu> {
    private static final ResourceLocation TEXTURE = Utility.rl("textures/gui/redstone_clock_gui.png");
    private static final ResourceLocation PROGRESS_ARROW = Utility.rl("progress_arrow");
    private EditBox maxProgressBox;
    private EditBox onTime;
    private EditBox signalStrength;

    public RedstoneClockScreen(RedstoneClockMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {

        super.init();
        this.clearWidgets();

        this.maxProgressBox = new EditBox(this.font, this.leftPos + 10, this.topPos + 53, 38, 12, Component.literal("Max Progress"));
        this.maxProgressBox.setMaxLength(5);
        this.maxProgressBox.setValue(String.valueOf(menu.blockEntity.getMaxProgress()));

        this.onTime = new EditBox(this.font, this.leftPos + 128, this.topPos + 53, 38, 12, Component.literal("On Time"));
        this.onTime.setMaxLength(5);
        this.onTime.setValue(String.valueOf(menu.blockEntity.getOnTime()));

        this.signalStrength = new EditBox(this.font, this.leftPos + 76, this.topPos + 53, 20, 12, Component.literal("Signal Strength"));
        this.signalStrength.setMaxLength(2);
        this.signalStrength.setValue(String.valueOf(menu.blockEntity.getSignalStrength()));

        this.maxProgressBox.setResponder(this::onSearchChanged);
        this.onTime.setResponder(this::onSearchChanged);
        this.signalStrength.setResponder(this::onSearchChanged);

    }

    private void onSearchChanged(String text) {
        int maxValue = parseSafe(maxProgressBox);
        int onTimeValue = parseSafe(onTime);

        int signalStrengthValue = parseSafe(signalStrength);
        signalStrengthValue = Math.max(1, Math.min(15, signalStrengthValue));

        ClientPacketDistributor.sendToServer(new SyncRedstoneClockPacket(menu.blockPos, maxValue, onTimeValue, signalStrengthValue));
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
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.maxProgressBox != null) this.maxProgressBox.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.onTime != null) this.onTime.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.signalStrength != null) this.signalStrength.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.maxProgressBox != null && this.maxProgressBox.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.redstone_clock.max_progress", mouseX, mouseY);
        } else if (this.onTime != null && this.onTime.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.redstone_clock.on_time", mouseX, mouseY);
        } else if (this.signalStrength != null && this.signalStrength.isMouseOver(mouseX, mouseY)) {
            renderTooltip(guiGraphics, "tooltip.redstone_clock.signal_strength", mouseX, mouseY);
        }
    }

    private void renderTooltip(GuiGraphics guiGraphics, String text, int mouseX, int mouseY) {
        List<ClientTooltipComponent> components =
                Collections.singletonList(ClientTooltipComponent.create(Component.translatable(text).getVisualOrderText()));
        guiGraphics.renderTooltip(Minecraft.getInstance().font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.maxProgressBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (this.onTime.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (this.signalStrength.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.maxProgressBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.onTime.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.signalStrength.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.maxProgressBox.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.maxProgressBox);
            return true;
        }
        if (this.onTime.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.onTime);
            return true;
        }
        if (this.signalStrength.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.signalStrength);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int parseSafe(EditBox box) {
        String value = box.getValue();
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
