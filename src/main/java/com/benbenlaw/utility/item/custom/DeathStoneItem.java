package com.benbenlaw.utility.item.custom;

import com.benbenlaw.utility.item.UtilityDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Consumer;

public class DeathStoneItem extends Item {
    public DeathStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && stack.has(UtilityDataComponents.GLOBAL_POS.get())) {

            GlobalPos globalPos = stack.get(UtilityDataComponents.GLOBAL_POS.get());

            assert globalPos != null;
            player.teleportTo(
                    Objects.requireNonNull(Objects.requireNonNull(player.level().getServer()).getLevel(globalPos.dimension())),
                    globalPos.pos().getX() + 0.5,
                    globalPos.pos().getY() + 0.5,
                    globalPos.pos().getZ() + 0.5,
                    EnumSet.noneOf(Relative.class),
                    player.getYRot(),
                    player.getXRot(),
                    false
            );

            stack.shrink(1);
            player.playNotifySound(SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.2f, 1);
            player.displayClientMessage(Component.translatable("tooltip.death_stone.returning").withStyle(ChatFormatting.GREEN), false);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> consumer, TooltipFlag flag) {
        if (Minecraft.getInstance().hasShiftDown()) {
            consumer.accept(Component.translatable("tooltip.death_stone.info").withStyle(ChatFormatting.BLUE));
        } else {
            consumer.accept(Component.translatable("tooltip.bblcore.shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}
