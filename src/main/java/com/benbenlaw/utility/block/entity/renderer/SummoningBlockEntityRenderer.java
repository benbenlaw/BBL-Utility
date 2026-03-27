package com.benbenlaw.utility.block.entity.renderer;

import com.benbenlaw.utility.block.custom.SummoningBlock;
import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.block.entity.SummoningBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SummoningBlockEntityRenderer implements BlockEntityRenderer<SummoningBlockEntity, SummoningBlockRenderState> {

    private final EntityRenderDispatcher entityDispatcher;

    public SummoningBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    public SummoningBlockRenderState createRenderState() {
        return new SummoningBlockRenderState();
    }

    @Override
    public void extractRenderState(SummoningBlockEntity blockEntity, SummoningBlockRenderState renderState,
                                   float partialTick, @NotNull net.minecraft.world.phys.Vec3 cameraPosition,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        // Store block position & level in render state
        renderState.blockEntityLevel = blockEntity.getLevel();
        renderState.lightPosition = blockEntity.getBlockPos();
        renderState.growthProgress = 1f; // full size
    }

    @Override
    public void submit(SummoningBlockRenderState renderState, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {

        Level level = renderState.blockEntityLevel;
        BlockPos pos = renderState.lightPosition;
        if (level == null || pos == null) return;

        // --- Push pose ---
        poseStack.pushPose();

        // Center the cow on the block
        poseStack.translate(0.5, 1.0, 0.5);

        // Optional: rotate based on block facing
        float yaw = 0;
        Direction facing = level.getBlockState(pos).getValue(SummoningBlock.FACING);
        switch (facing) {
            case NORTH -> yaw = 180;
            case SOUTH -> yaw = 0;
            case EAST -> yaw = 90;
            case WEST -> yaw = -90;
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // Scale based on growth
        float scale = renderState.growthProgress;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0f, -0.5f * (1.0f - scale), 0.0f);

        // --- Render the cow ---
        Cow cow = new Cow(EntityType.COW, level);
        cow.setPos(0,0,0); // local block coords
        cow.tickCount = (int) level.getGameTime();

        var cowState = entityDispatcher.getRenderer(cow).createRenderState(cow, 0.0f);

        double camX = cowState.x - cameraRenderState.pos.x();
        double camY = cowState.y - cameraRenderState.pos.y();
        double camZ = cowState.z - cameraRenderState.pos.z();

        entityDispatcher.submit(cowState, cameraRenderState, camX, camY, camZ, poseStack, submitNodeCollector);

        poseStack.popPose();
    }

}