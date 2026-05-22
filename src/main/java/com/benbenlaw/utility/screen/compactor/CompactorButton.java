package com.benbenlaw.utility.screen.compactor;


import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.CompactorBlockEntity;
import com.benbenlaw.utility.network.packets.SyncCompactorMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CompactorButton extends Button {
    private boolean is3x3;

    public CompactorButton(int x, int y, int width, int height, boolean initial, Button.OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.is3x3 = initial;
        this.height = 18;
        this.width = 18;
    }

    public void toggle() {
        this.is3x3 = !this.is3x3;
    }

    public void setOverrideRenderHighlightedSprite(Supplier<Boolean> overrideRenderHighlightedSprite) {
        super.setOverrideRenderHighlightedSprite(overrideRenderHighlightedSprite);
    }

    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.isHovered();
        Identifier currentTexture = this.is3x3
                ? (hovered ? Utility.identifier("compactor/compactor3x3_hover") : Utility.identifier("compactor/compactor3x3"))
                : (hovered ? Utility.identifier("compactor/compactor2x2_hover") : Utility.identifier("compactor/compactor2x2"));

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, currentTexture, this.getX(), this.getY(), this.width, this.height);

        if (isHovered) {
            Component modeText = Component.translatable(this.is3x3
                    ? "tooltip.utility.compactor_button.is3x3"
                    : "tooltip.utility.compactor_button.is2x2"
            ).withStyle(ChatFormatting.GREEN);

            Component tooltip = Component.translatable("tooltip.utility.compactor_button.mode", modeText);
            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    public static CompactorButton create(int x, int y, int width, int height, BlockEntity blockEntity) {
        if (blockEntity instanceof CompactorBlockEntity compactorBlockEntity) {
            boolean initialMode = compactorBlockEntity.is3x3();
            return new CompactorButton(x, y, width, height, initialMode, (button) -> {
                CompactorButton whitelistButton = (CompactorButton)button;
                whitelistButton.toggle();
                compactorBlockEntity.toggleCraftingMode();
                ClientPacketDistributor.sendToServer(new SyncCompactorMode(blockEntity.getBlockPos()));
            });
        } else {
            Utility.LOGGER.error("Attempted to create button for a Compactor that is not a compactor: " + String.valueOf(blockEntity));
            return null;
        }
    }
}
