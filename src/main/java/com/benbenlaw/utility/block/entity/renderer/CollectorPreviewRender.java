package com.benbenlaw.utility.block.entity.renderer;

import com.benbenlaw.utility.block.entity.ItemCollectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;

public class CollectorPreviewRender implements CustomBlockOutlineRenderer {

    private final Camera camera;

    public CollectorPreviewRender(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean render(BlockOutlineRenderState renderState, MultiBufferSource.BufferSource buffer, PoseStack poseStack, boolean translucentPass, LevelRenderState levelRenderState) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || ClientRenderState.get() == null) return false;

        BlockPos pos = ClientRenderState.get();
        BlockEntity be = mc.level.getBlockEntity(pos);

        if (!(be instanceof ItemCollectorBlockEntity collector)) return false;

        AABB box = collector.createArea();

        VertexConsumer lineBuilder = buffer.getBuffer(RenderTypes.lines());

        double camX = camera.position().x;
        double camY = camera.position().y;
        double camZ = camera.position().z;

        AABB shifted = box.move(-camX, -camY, -camZ);

        ShapeRenderer.renderShape(
                poseStack,
                lineBuilder,
                Shapes.create(shifted),
                0, 1, 0,
                ARGB.colorFromFloat(0.4F, 0, 1, 0),
                2F
        );

        return false;
    }
}