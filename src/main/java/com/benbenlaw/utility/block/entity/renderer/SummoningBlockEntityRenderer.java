package com.benbenlaw.utility.block.entity.renderer;

import com.benbenlaw.utility.block.custom.SummoningBlock;
import com.benbenlaw.utility.block.entity.SummoningBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class SummoningBlockEntityRenderer implements BlockEntityRenderer<@NotNull SummoningBlockEntity, @NotNull SummoningBlockRenderState> {

    private final EntityRenderDispatcher entityRenderState;

    public SummoningBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        entityRenderState = context.entityRenderer();

    }

    @Override
    public @NotNull SummoningBlockRenderState createRenderState() {
        return new SummoningBlockRenderState();
    }

    @Override
    public void extractRenderState(SummoningBlockEntity blockEntity, SummoningBlockRenderState renderState,
                                   float partialTick, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {

        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        if (blockEntity.getLevel() == null) return;

        renderState.renderEntity = blockEntity.getSummonedEntity();
        renderState.entityTag = blockEntity.getSummonedEntityData().orElse(null);
        renderState.facing = blockEntity.getBlockState().getValue(SummoningBlock.FACING);
        renderState.scaledProgress = blockEntity.getScaledProgress();
    }

    @Override
    public void submit(@NotNull SummoningBlockRenderState state,
                       PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector,
                       CameraRenderState cameraRenderState) {

        poseStack.pushPose();

        // Center on block
        poseStack.translate(0.5f, 1.0f, 0.5f);

        float scale = state.scaledProgress;

        // Prevent invalid scale (important to avoid NaNs / invisible rendering)
        scale = Math.max(0.0001f, scale);

        // Scale entity
        poseStack.scale(scale, scale, scale);

        // Keep entity grounded while scaling
        poseStack.translate(0.0f, -0.5f * (1.0f - scale), 0.0f);

        // Facing rotation
        Direction direction = state.facing;
        float yaw = switch (direction) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case EAST -> 90f;
            case WEST -> -90f;
            default -> 0f;
        };

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // Get entity type
        EntityType<?> type = state.renderEntity;

        if (type != null && Minecraft.getInstance().level != null) {

            // Create entity instance
            Entity entity = type.create(
                    Minecraft.getInstance().level,
                    EntitySpawnReason.EVENT
            );

            if (entity != null) {

                // Apply saved NBT data (THIS is what makes it the "exact mob")
                if (state.entityTag != null) {
                    ValueInput input = TagValueInput.create(
                            ProblemReporter.DISCARDING,
                            Minecraft.getInstance().level.registryAccess(),
                            state.entityTag
                    );

                    entity.load(input);
                }

                // Extract render state
                EntityRenderState renderState =
                        entityRenderState.extractEntity(entity, 0.0f);

                // Submit to renderer
                entityRenderState.submit(
                        renderState,
                        cameraRenderState,
                        0.0d,
                        0.0d,
                        0.0d,
                        poseStack,
                        submitNodeCollector
                );
            }
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(@NotNull SummoningBlockEntity blockEntity, Vec3 cameraPosition) {
        return true;
    }
}