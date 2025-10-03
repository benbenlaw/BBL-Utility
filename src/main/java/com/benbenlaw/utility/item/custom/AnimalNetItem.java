package com.benbenlaw.utility.item.custom;

import com.benbenlaw.core.util.DirectionUtil;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.item.UtilityDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class AnimalNetItem extends Item {
    public AnimalNetItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {

        Level level = player.level();

        boolean canCaptureHostileMobs = interactionTarget instanceof Monster && UtilityStartUpConfig.animalNetHostileMobs.get();
        boolean canCaptureWaterMobs = interactionTarget instanceof WaterAnimal && UtilityStartUpConfig.animalNetWaterMobs.get();
        boolean canCaptureAnimalMobs = interactionTarget instanceof Animal && UtilityStartUpConfig.animalNetAnimalMobs.get();
        boolean canCaptureVillagerMobs = interactionTarget instanceof Villager && UtilityStartUpConfig.animalNetVillagerMobs.get();

        boolean isValidTarget = canCaptureHostileMobs || canCaptureWaterMobs || canCaptureAnimalMobs || canCaptureVillagerMobs;

        if (!level.isClientSide()) {

            if (isValidTarget) {

                TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
                interactionTarget.saveWithoutId(tagValueOutput);
                CompoundTag nbt = tagValueOutput.buildResult();
                stack.set(UtilityDataComponents.ENTITY_TYPE.get(), EntityType.getKey(interactionTarget.getType()).toString());
                stack.set(UtilityDataComponents.ENTITY_DATA.get(), nbt);

                String mobName = interactionTarget.getName().getString();
                player.displayClientMessage(Component.translatable("tooltip.animal_net.mob_caught", mobName).withStyle(ChatFormatting.GREEN), false);
                level.playSound(null, interactionTarget.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS);

                interactionTarget.remove(Entity.RemovalReason.DISCARDED);

            } else {
                player.displayClientMessage(Component.translatable("tooltip.animal_net.mob_cannot_be_caught").withStyle(ChatFormatting.RED), false);
            }
            return InteractionResult.SUCCESS;

        }
        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        assert player != null;
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();

        if (!level.isClientSide()) {
            if (stack.has(UtilityDataComponents.ENTITY_TYPE.get()) && stack.has(UtilityDataComponents.ENTITY_DATA.get())) {

                String entityTypeString = stack.get(UtilityDataComponents.ENTITY_TYPE.get());
                CompoundTag entityData = stack.get(UtilityDataComponents.ENTITY_DATA.get());
                assert entityTypeString != null;
                EntityType<?> entityType = EntityType.byString(entityTypeString).orElse(null);

                if (entityType != null) {
                    Entity entity = entityType.create(level, EntitySpawnReason.SPAWN_ITEM_USE);

                    assert entityData != null;
                    ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), entityData);

                    assert entity != null;
                    entity.load(valueInput);

                    entity.setPos(Vec3.atBottomCenterOf(pos.relative(direction)));
                    stack.remove(UtilityDataComponents.ENTITY_TYPE);
                    stack.remove(UtilityDataComponents.ENTITY_DATA);

                    level.addFreshEntity(entity);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS);

                    if (UtilityStartUpConfig.animalNetTakesDamage.get()) {
                        stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                    }

                } else {
                    player.displayClientMessage(Component.translatable("tooltip.animal_net.could_not_release").withStyle(ChatFormatting.RED), false);
                }

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> consumer, TooltipFlag flag) {

        if (Minecraft.getInstance().hasShiftDown()) {
            consumer.accept(Component.translatable("tooltip.animal_net.info").withStyle(ChatFormatting.BLUE));

            if (stack.get(UtilityDataComponents.ENTITY_TYPE.get()) != null) {

                EntityType<?> entity = EntityType.byString(Objects.requireNonNull(stack.get(UtilityDataComponents.ENTITY_TYPE.get())))
                        .orElse(null);

                String mobName = entity != null ? entity.getDescription().getString() : "tooltip.animal_net.unknown_mob";
                consumer.accept(Component.translatable("tooltip.animal_net.contains", mobName).withStyle(ChatFormatting.GREEN));

            } else {

                boolean hostileMobs = UtilityStartUpConfig.animalNetHostileMobs.get();
                boolean waterMobs = UtilityStartUpConfig.animalNetWaterMobs.get();
                boolean animalMobs = UtilityStartUpConfig.animalNetAnimalMobs.get();
                boolean villagerMobs = UtilityStartUpConfig.animalNetVillagerMobs.get();

                consumer.accept(Component.translatable("tooltip.animal_net.mob_types").withStyle(ChatFormatting.BLUE));

                if (hostileMobs) {
                    consumer.accept(Component.translatable("tooltip.animal_net.hostile_mobs").withStyle(ChatFormatting.GREEN));
                }

                if (waterMobs) {
                    consumer.accept(Component.translatable("tooltip.animal_net.water_mobs").withStyle(ChatFormatting.GREEN));
                }

                if (animalMobs) {
                    consumer.accept(Component.translatable("tooltip.animal_net.animal_mobs").withStyle(ChatFormatting.GREEN));
                }

                if (villagerMobs) {
                    consumer.accept(Component.translatable("tooltip.animal_net.villager_mobs").withStyle(ChatFormatting.GREEN));
                }


            }
        } else {
            consumer.accept(Component.translatable("tooltip.bblcore.shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}
