package com.benbenlaw.utility.block.entity.renderer;

import com.benbenlaw.utility.block.entity.SummoningBlockEntity;
import com.benbenlaw.utility.screen.summoning.SummoningBlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Optional;

public class SummoningBlockRenderState extends BlockEntityRenderState {

    public EntityType<?> renderEntity;
    public CompoundTag entityTag;
    public Direction facing;
    public float scaledProgress;
}