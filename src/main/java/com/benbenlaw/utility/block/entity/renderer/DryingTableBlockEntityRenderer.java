package com.benbenlaw.utility.block.entity.renderer;

import com.benbenlaw.core.util.FluidRendererUtil;
import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.NotNull;

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

        renderState.fluidStack = FluidUtil.getStack(blockEntity.getFluidHandler(), 0);
        renderState.tankCapacity = blockEntity.getFluidHandler().getCapacityAsInt(0, FluidResource.of(renderState.fluidStack));

        renderState.lightPosition = blockEntity.getBlockPos();
        renderState.blockEntityLevel = blockEntity.getLevel();
        renderState.rotation = getRenderingRotation();

        ItemStack renderItem = ItemStack.EMPTY;

        if (!blockEntity.getItemHandler().getResource(DryingTableBlockEntity.OUTPUT_SLOT).toStack().isEmpty()) {
            renderItem = blockEntity.getItemHandler().getResource(DryingTableBlockEntity.INPUT_SLOT).toStack();
        }

        else if (!blockEntity.getItemHandler().getResource(DryingTableBlockEntity.INPUT_SLOT).toStack().isEmpty()) {
            renderItem = blockEntity.getItemHandler().getResource(DryingTableBlockEntity.INPUT_SLOT).toStack();
        }

        itemModelResolver.updateForTopItem(renderState.itemStackRenderState,
                renderItem, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
    }



    @Override
    public void submit(DryingTableRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {

        float fillRatio = renderState.tankCapacity == 0
                ? 0
                : renderState.fluidStack.getAmount() / (float) renderState.tankCapacity;

        //Item
        poseStack.pushPose();
        if (fillRatio <= 0.4) {
            poseStack.translate(0.5f, 0.2f, 0.5f);
        }
        else {
            poseStack.translate(0.5f, fillRatio - 0.2f, 0.5f);
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotation));
        renderState.itemStackRenderState.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        //Fluids
        poseStack.pushPose();
        poseStack.translate(0, 0.0, 0);
        FluidRendererUtil.submitFluid(poseStack, Sheets.translucentBlockItemSheet(), submitNodeCollector, renderState.fluidStack, fillRatio, renderState.lightCoords);
        poseStack.popPose();

    }

    public float getRenderingRotation() {
        rotation += 0.1f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

}