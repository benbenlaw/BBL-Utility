package com.benbenlaw.utility.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class FloaterItem extends Item {
    public FloaterItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {

        ItemStack offHand = player.getOffhandItem();
        ItemStack mainHand = player.getMainHandItem();

        if (!level.isClientSide()) {
            if (offHand.is(this) && mainHand.getItem() instanceof BlockItem blockItem) {

                Vec3 lookVec = player.getLookAngle().normalize().scale(3.0);
                BlockPos blockPos = player.blockPosition().offset((int) Math.round(lookVec.x), (int) Math.round(lookVec.y) + 1, (int) Math.round(lookVec.z));

                if (level.getBlockState(blockPos).isAir() || level.getBlockState(blockPos).is(Blocks.WATER)) {
                    level.setBlockAndUpdate(blockPos, blockItem.getBlock().defaultBlockState());
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> consumer, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            consumer.accept(Component.translatable("tooltip.floater.info").withStyle(ChatFormatting.BLUE));
        } else {
            consumer.accept(Component.translatable("tooltip.bblcore.shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}
