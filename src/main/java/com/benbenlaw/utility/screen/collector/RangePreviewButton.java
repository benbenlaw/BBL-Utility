package com.benbenlaw.utility.screen.collector;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.renderer.ClientRenderState;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Toggles the collector's range outline, the same state shift-right-clicking the block sets */
public class RangePreviewButton extends Button {

    private final BlockPos pos;

    public RangePreviewButton(int x, int y, int width, int height, BlockPos pos) {
        super(x, y, width, height, Component.empty(),
                button -> ClientRenderState.toggleCollector(pos), DEFAULT_NARRATION);
        this.pos = pos;
    }

    private boolean isShowing() {
        return this.pos.equals(ClientRenderState.get());
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.isHovered();
        boolean showing = this.isShowing();

        Identifier currentTexture = showing
                ? (hovered ? Utility.identifier("item_collector/range_shown_hover") : Utility.identifier("item_collector/range_shown"))
                : (hovered ? Utility.identifier("item_collector/range_hidden_hover") : Utility.identifier("item_collector/range_hidden"));

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, currentTexture, this.getX(), this.getY(), this.width, this.height);

        if (hovered) {
            Component modeText = Component.translatable(showing
                    ? "tooltip.utility.range_button.shown"
                    : "tooltip.utility.range_button.hidden"
            ).withStyle(showing ? ChatFormatting.GREEN : ChatFormatting.GRAY);

            Component tooltip = Component.translatable("tooltip.utility.range_button.mode", modeText);
            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }
}
