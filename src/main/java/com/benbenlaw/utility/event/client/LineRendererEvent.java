package com.benbenlaw.utility.event.client;

import com.benbenlaw.core.util.TooltipUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.ItemCollectorBlockEntity;
import com.benbenlaw.utility.block.entity.renderer.ClientRenderState;
import com.benbenlaw.utility.item.UtilityItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class LineRendererEvent {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentBlocks event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || ClientRenderState.get() == null) return;

        BlockPos pos = ClientRenderState.get();
        BlockEntity be = mc.level.getBlockEntity(pos);
        if (!(be instanceof ItemCollectorBlockEntity collector)) return;

        AABB box = collector.createArea();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        Vec3 cam = mc.gameRenderer.getMainCamera().position();

        VertexConsumer lineBuilder = buffer.getBuffer(RenderTypes.lines());

        AABB shifted = box.move(-cam.x, -cam.y, -cam.z);

        ShapeRenderer.renderShape(poseStack, lineBuilder, Shapes.create(shifted), 0, 1, 0,  ARGB.colorFromFloat(0.4F, 0, 0, 0),2f);

        buffer.endBatch();
    }
}
