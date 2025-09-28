package com.benbenlaw.utility.item.custom;

import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.data.UtilityBlockTags;
import com.benbenlaw.utility.util.UtilityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SaplingGrowerItem extends Item {
    public SaplingGrowerItem(Properties properties) {
        super(properties);
    }

    public int TOTAL_GROWTH_ATTEMPTS = UtilityStartUpConfig.totalGrowthAttempts.get();

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (!level.isClientSide()) {

            if (state.is(UtilityTags.Blocks.VALID_FOR_SAPLING_GROWER)) {
                tryToGrow(level, pos);
                if (UtilityStartUpConfig.saplingGrowerTakesDamage.get()) {
                    assert player != null;
                    stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    public void tryToGrow(Level level, BlockPos pos) {
        for (int i = 0; i < TOTAL_GROWTH_ATTEMPTS; i++) {
            doGrow(level, pos);
        }
    }

    public void doGrow(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (!level.isClientSide && blockState.getBlock() instanceof BonemealableBlock bonemealableBlock)
            bonemealableBlock.performBonemeal((ServerLevel) level, level.random, pos, blockState);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> consumer, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            consumer.accept(Component.translatable("tooltip.sapling_grower.info").withStyle(ChatFormatting.BLUE));
        } else {
            consumer.accept(Component.translatable("tooltip.bblcore.shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}
