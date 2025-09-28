package com.benbenlaw.utility.item.custom;

import com.benbenlaw.core.item.CoreItemUtils;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class CrookItem extends Item {

    public CrookItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        if (state.getBlock() instanceof LeavesBlock || state.is(BlockTags.LEAVES)) {

            int rolls = UtilityStartUpConfig.crookRolls.get();

            for (int i = 0; i < rolls; i++) {
                List<ItemStack> blockDrops = Block.getDrops(state.getBlock().defaultBlockState(), (ServerLevel) level, pos,
                        level.getBlockEntity(pos), null, this.getDefaultInstance());

                for (ItemStack drop : blockDrops) {
                    CoreItemUtils.popItemStack(level, pos, drop);
                }
            }

            if (UtilityStartUpConfig.crookTakesDamage.get()) {
                stack.hurtAndBreak(1, entity, entity.getEquipmentSlotForItem(stack));
            }
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        if (state.getBlock() instanceof LeavesBlock || state.is(BlockTags.LEAVES)) {
            return 10f;
        }
        return super.getDestroySpeed(stack, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> consumer, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            consumer.accept(Component.translatable("tooltip.crook.info").withStyle(ChatFormatting.BLUE));
        } else {
            consumer.accept(Component.translatable("tooltip.bblcore.shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}

