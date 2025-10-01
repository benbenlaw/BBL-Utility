package com.benbenlaw.utility.screen.breaker;

import com.benbenlaw.core.screen.util.button.WhitelistButton;
import com.benbenlaw.utility.Utility;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;


public class BlockBreakerScreen extends AbstractContainerScreen<BlockBreakerMenu> {
    private static final ResourceLocation TEXTURE = Utility.rl("textures/gui/block_breaker_gui.png");
    private static final ResourceLocation PROGRESS_ARROW = Utility.rl("progress_arrow");

    public BlockBreakerScreen(BlockBreakerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        addRenderableWidget(WhitelistButton.create(x + 151, y + 52, 20, 20, menu.blockEntity));

        /*
        boolean initialMode = menu.blockEntity.isWhitelist();
        Button whitelistButton = new WhitelistButton(
                x + 151,
                y + 52,
                20, 20,
                initialMode,
                button -> {
                    ((WhitelistButton) button).toggle();
                    boolean newMode = !menu.blockEntity.isWhitelist();
                    menu.blockEntity.setWhitelist(newMode);
                    ClientPacketDistributor.sendToServer(new SyncWhitelistMode(menu.blockEntity.getBlockPos(), newMode));
                }
        );

         */

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
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
