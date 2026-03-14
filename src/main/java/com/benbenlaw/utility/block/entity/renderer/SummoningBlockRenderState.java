package com.benbenlaw.utility.block.entity.renderer;

import com.benbenlaw.utility.screen.summoning.SummoningBlockScreen;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class SummoningBlockRenderState extends BlockEntityRenderState {

    public EntityRenderState entityRenderState;
    public float growthProgress;

    public BlockPos lightPosition;
    public Level blockEntityLevel;

    public SummoningBlockRenderState() {
        this.entityRenderState = new EntityRenderState();
        this.growthProgress = 0f;
    }

    public void setupEntity(EntityType<?> type, BlockPos pos, Level level, float growthProgress) {
        this.entityRenderState.entityType = type;
        this.entityRenderState.x = pos.getX() + 0.5;
        this.entityRenderState.y = pos.getY() + 0.5;
        this.entityRenderState.z = pos.getZ() + 0.5;
        this.entityRenderState.ageInTicks = 0;
        this.entityRenderState.lightCoords = blockEntityLevel.getLightEmission(pos);
        this.lightPosition = pos;
        this.blockEntityLevel = level;
        this.growthProgress = growthProgress;
    }
}