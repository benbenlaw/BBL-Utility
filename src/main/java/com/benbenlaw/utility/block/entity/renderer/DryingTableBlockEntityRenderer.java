package com.benbenlaw.utility.block.entity.renderer;

import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class DryingTableBlockEntityRenderer implements BlockEntityRenderer<@NotNull DryingTableBlockEntity, @NotNull DryingTableRenderState> {
    private final ItemModelResolver itemModelResolver;
    private float rotation;

    public DryingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public DryingTableRenderState createRenderState() {
        return new DryingTableRenderState();
    }

    @Override
    public void extractRenderState(DryingTableBlockEntity blockEntity, DryingTableRenderState renderState, float partialTick,
                                   @NotNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.lightPosition = blockEntity.getBlockPos();
        renderState.blockEntityLevel = blockEntity.getLevel();
        renderState.rotation = getRenderingRotation();

        ItemStack renderItem = ItemStack.EMPTY;

        if (!blockEntity.getOutputHandler().getResource(DryingTableBlockEntity.OUTPUT_SLOT).toStack().isEmpty()) {
            renderItem = blockEntity.getOutputHandler().getResource(DryingTableBlockEntity.INPUT_SLOT).toStack();
        }

        else if (!blockEntity.getInputHandler().getResource(DryingTableBlockEntity.INPUT_SLOT).toStack().isEmpty()) {
            renderItem = blockEntity.getInputHandler().getResource(DryingTableBlockEntity.INPUT_SLOT).toStack();
        }

        itemModelResolver.updateForTopItem(renderState.itemStackRenderState,
                renderItem, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
    }

    @Override
    public void submit(DryingTableRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotation));

        renderState.itemStackRenderState.submit(poseStack, submitNodeCollector, getLightLevel(renderState.blockEntityLevel,
                renderState.lightPosition), OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

    public float getRenderingRotation() {
        rotation += 0.1f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

}