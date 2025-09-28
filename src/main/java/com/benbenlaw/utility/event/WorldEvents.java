package com.benbenlaw.utility.event;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.data.UtilityLangProvider;
import com.benbenlaw.utility.item.UtilityDataComponents;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Objects;

@EventBusSubscriber(modid = Utility.MOD_ID)
public class WorldEvents {

    @SubscribeEvent
    public static void removeEventAfterTesting(PlayerEvent.BreakSpeed event) {
        //System.out.println(event.getOriginalSpeed());

    }



    @SubscribeEvent
    public static void addDeathStoneOnPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {

        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {

            if (event.isEndConquered()) return;

            ItemStack stack = new ItemStack(UtilityItems.DEATH_STONE.get());
            GlobalPos pos = player.getLastDeathLocation().orElse(new GlobalPos(Level.OVERWORLD, BlockPos.ZERO));
            stack.set(UtilityDataComponents.GLOBAL_POS.get(), pos);

            if (UtilityStartUpConfig.shouldPlayerGetDeathStoneOnDeath.get()) {

                if (Objects.requireNonNull(level.getServer()).getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get()) {
                    level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack));
                } else {
                    player.addItem(stack);
                }
            }
        }
    }
}
