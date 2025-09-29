package com.benbenlaw.utility.block.custom;

import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DryingTableBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<DryingTableBlock> CODEC = simpleCodec(DryingTableBlock::new);
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public @NotNull MapCodec<DryingTableBlock> codec() {
        return CODEC;
    }

    public DryingTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(RUNNING, false).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof DryingTableBlockEntity entity1) {
                player.openMenu(new SimpleMenuProvider(entity1, entity1.getDisplayName()), pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @Nullable Orientation orientation, boolean p_60514_) {

        if (!level.isClientSide()) {
            boolean powered = level.hasNeighborSignal(pos);
            if (powered && state.getValue(RUNNING)) {
                level.setBlock(pos, state.setValue(RUNNING, false), 3);
            } else if (!powered && !state.getValue(RUNNING)) {
                level.setBlock(pos, state.setValue(RUNNING, true), 3);
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockPos);
        Direction direction = context.getHorizontalDirection().getOpposite();

        if (blockState.is(Blocks.WATER)) {
            return this.defaultBlockState().setValue(WATERLOGGED, true).setValue(FACING, direction).setValue(RUNNING, false);
        } else {
            return this.defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, direction).setValue(RUNNING, false);
        }
    }

    @Override
    public @NotNull BlockState rotate(BlockState blockState, @NotNull LevelAccessor level, @NotNull BlockPos blockPos, Rotation direction) {
        return blockState.setValue(RUNNING, blockState.getValue(RUNNING)).setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)).setValue(FACING, direction.rotate(blockState.getValue(FACING)));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(RUNNING, WATERLOGGED, FACING);
    }

    public @NotNull FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DryingTableBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, UtilityBlockEntities.DRYING_TABLE_BLOCK_ENTITY.get(),
                (thisLevel, thisPos, thisState, thisEntity) -> thisEntity.tick());
    }
}
