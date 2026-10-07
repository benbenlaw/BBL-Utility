package com.benbenlaw.utility.util;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;
import java.util.Optional;

public class ClickerUtil {

    public static void prepare(FakePlayer player) {
        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) attackSpeed.setBaseValue(1024.0);
    }

    public static int attackDelay(ItemStack stack) {
        double[] speed = {4.0};
        if (!stack.isEmpty()) {
            stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
                if (attribute.is(Attributes.ATTACK_SPEED) && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    speed[0] += modifier.amount();
                }
            });
        }
        return (int) Math.ceil(20.0 / Math.max(speed[0], 0.1));
    }

    public static void setup(FakePlayer player, Vec3 eyePos, Direction direction, ItemStack toHold, boolean sneaking) {
        player.getInventory().setItem(player.getInventory().getSelectedSlot(), toHold);
        float xRot = direction == Direction.DOWN ? 90 : direction == Direction.UP ? -90 : 0;
        player.setShiftKeyDown(sneaking);
        player.snapTo(eyePos.x, eyePos.y - player.getEyeHeight(), eyePos.z);
        player.setYRot(direction.toYRot());
        player.setXRot(xRot);
        player.setYHeadRot(direction.toYRot());
        player.yHeadRotO = direction.toYRot();
        if (!toHold.isEmpty()) {
            toHold.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
                AttributeInstance instance = player.getAttributes().getInstance(attribute);
                if (instance != null) {
                    instance.removeModifier(modifier.id());
                    instance.addTransientModifier(modifier);
                }
            });
        }
    }

    public static void cleanup(FakePlayer player, ItemStack oldStack) {
        if (!oldStack.isEmpty()) {
            oldStack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
                AttributeInstance instance = player.getAttributes().getInstance(attribute);
                if (instance != null) instance.removeModifier(modifier);
            });
        }
        player.getInventory().setItem(player.getInventory().getSelectedSlot(), ItemStack.EMPTY);
        if (!player.getInventory().isEmpty()) player.getInventory().dropAll();
        player.setShiftKeyDown(false);
    }

    public static boolean rightClick(FakePlayer player, ServerLevel level) {
        ItemStack stack = player.getMainHandItem();
        HitResult hit = rayTrace(player, level);

        if (hit instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (player.interactOn(entity, InteractionHand.MAIN_HAND, entityHit.getLocation().subtract(entity.position())).consumesAction())
                return true;
        } else if (hit instanceof BlockHitResult blockHit && blockHit.getType() == HitResult.Type.BLOCK) {
            if (player.gameMode.useItemOn(player, level, stack, InteractionHand.MAIN_HAND, blockHit).consumesAction())
                return true;
        }

        return !stack.isEmpty() && player.gameMode.useItem(player, level, stack, InteractionHand.MAIN_HAND).consumesAction();
    }

    public static boolean leftClick(FakePlayer player, ServerLevel level) {
        HitResult hit = rayTrace(player, level);

        if (hit instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (entity instanceof ItemEntity || entity instanceof ExperienceOrb || entity instanceof Arrow) return false;
            player.attack(entity);
            return true;
        }

        return false;
    }

    private static HitResult rayTrace(FakePlayer player, ServerLevel level) {
        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        Vec3 base = new Vec3(player.getX(), player.getEyeY(), player.getZ());
        Vec3 target = base.add(player.getLookAngle().scale(reach));

        HitResult block = level.clip(new ClipContext(base, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
        HitResult entity = traceEntities(player, level, base, target);

        if (entity != null && entity.getLocation().distanceTo(base) < block.getLocation().distanceTo(base)) {
            return entity;
        }
        return block;
    }

    private static HitResult traceEntities(FakePlayer player, ServerLevel level, Vec3 base, Vec3 target) {
        AABB search = new AABB(base, target).inflate(0.5);
        List<Entity> entities = level.getEntities(player, search, e -> EntitySelector.NO_SPECTATORS.test(e) && e.isPickable());

        Entity closest = null;
        Vec3 closestPos = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : entities) {
            AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> clip = box.clip(base, target);
            if (box.contains(base)) {
                return new EntityHitResult(entity, clip.orElse(base));
            }
            if (clip.isPresent()) {
                double dist = base.distanceTo(clip.get());
                if (dist < closestDist) {
                    closest = entity;
                    closestPos = clip.get();
                    closestDist = dist;
                }
            }
        }

        return closest == null ? null : new EntityHitResult(closest, closestPos);
    }
}
