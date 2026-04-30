package com.benbenlaw.utility.block.entity;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.WhitelistBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.FilterItemHandler;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.core.util.FakePlayerUtil;
import com.benbenlaw.utility.block.UtilityBlockEntities;
import com.benbenlaw.utility.block.custom.SummoningBlock;
import com.benbenlaw.utility.config.UtilityStartUpConfig;
import com.benbenlaw.utility.network.packets.SyncEntitySummoningBlockPacket;
import com.benbenlaw.utility.recipe.SummoningRecipeInput;
import com.benbenlaw.utility.recipe.UtilityRecipeTypes;
import com.benbenlaw.utility.recipe.custom.SummoningRecipe;
import com.benbenlaw.utility.screen.summoning.SummoningBlockMenu;
import com.benbenlaw.utility.util.TemperatureValues;
import com.benbenlaw.utility.util.UtilityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class SummoningBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = UtilityStartUpConfig.dryingTableMaxDuration.get();
    private int progress = 0;
    private final SyncableItemHandler inventory = new SyncableItemHandler(this,1,
            (i, stack) -> i == INPUT_SLOT,
            i -> false);

    public static final int INPUT_SLOT = 0;
    private RecipeHolder<SummoningRecipe> cachedRecipe;
    private TemperatureValues temperatureValve = TemperatureValues.TEMPERATE;
    private EntityType<?> summonedEntity;
    private Optional<CompoundTag> summonedEntityData = Optional.empty();

    public SummoningBlockEntity(BlockPos pos, BlockState state) {
        super(UtilityBlockEntities.SUMMONING_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SummoningBlockEntity.this.progress;
                    case 1 -> SummoningBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SummoningBlockEntity.this.progress = value;
                    case 1 -> SummoningBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void tick() {
        assert level != null;
        if (!level.isClientSide()) {

            if (!level.getBlockState(worldPosition).getValue(SummoningBlock.RUNNING)) return;
            if (!level.getBlockState(worldPosition.above()).is(Blocks.AIR)) return;

            if (cachedRecipe == null || !isRecipeStillValid(cachedRecipe)) {
                updateCachedRecipe();
            }

            ItemStack inputStack = ItemUtil.getStack(inventory, INPUT_SLOT);

            if (inputStack.isEmpty()) {
                progress = 0;
                sync();
                cachedRecipe = null;
                return;
            }

            if (cachedRecipe != null) {
                sync();
                PacketDistributor.sendToAllPlayers(new SyncEntitySummoningBlockPacket(worldPosition, cachedRecipe.value().summonedEntity(), cachedRecipe.value().entityData()));
                progress++;
                summonedEntity = cachedRecipe.value().summonedEntity();
                if (progress >= maxProgress) {
                    summonMob();

                }
            } else {
                progress = 0;
            }

        }
    }

    private void summonMob() {
        if (cachedRecipe == null || level == null) return;

        var recipe = cachedRecipe.value();

        Entity entity = recipe.summonedEntity().create(level, EntitySpawnReason.MOB_SUMMONED);
        if (entity == null) return;

        recipe.entityData().ifPresent(tag -> {
            ValueInput input = TagValueInput.create(
                    ProblemReporter.DISCARDING,
                    level.registryAccess(),
                    tag
            );
            entity.load(input);
        });

        entity.setPos(Vec3.atBottomCenterOf(worldPosition.above()));

        float yaw = level.getBlockState(worldPosition).getValue(SyncableBlock.FACING).toYRot();
        entity.setYBodyRot(yaw);
        entity.setYHeadRot(yaw);
        entity.setYRot(yaw);

        level.addFreshEntity(entity);

        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.open(null)) {
                inventory.extract(INPUT_SLOT, inventory.getResource(INPUT_SLOT), recipe.input().count(), tx);
                tx.commit();
            }
        });

        progress = 0;
        sync();
    }

    private void updateCachedRecipe() {
        if (level != null && level.getServer() != null) {
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(UtilityRecipeTypes.SUMMONING_TYPE.get(),
                    new SummoningRecipeInput(inventory, level.getBlockState(worldPosition.below()), temperatureValve), level).orElse(null);

            if (cachedRecipe != null) {
                setSummonedEntity(cachedRecipe.value().summonedEntity());
            }


        }
    }

    private boolean isRecipeStillValid(RecipeHolder<SummoningRecipe> recipeHolder) {
        if (level == null) return false;

        var recipe = recipeHolder.value();

        return recipe.matches(
                new SummoningRecipeInput(inventory, level.getBlockState(worldPosition.below()), temperatureValve),
                level
        );
    }

    public void updateTemperatureValve() {
        if (level == null) return;

        boolean foundIce = false;

        for (Direction dir : Direction.values()) {
            BlockPos pos = worldPosition.relative(dir);
            BlockState state = level.getBlockState(pos);

            if (state.is(UtilityTags.Blocks.HOT_BLOCKS)) {
                temperatureValve = TemperatureValues.WARM;
                return;
            }

            if (state.is(UtilityTags.Blocks.COLD_BLOCKS)) {
                foundIce = true;
            }
        }

        if (foundIce) {
            this.temperatureValve = TemperatureValues.COLD;
        } else {
            this.temperatureValve = TemperatureValues.TEMPERATE;
        }
    }

    public ItemStacksResourceHandler getItemHandler() {
        return inventory;
    }

    public EntityType<?> getSummonedEntity() {
        return summonedEntity;
    }

    public Optional<CompoundTag> getSummonedEntityData() {
        return summonedEntityData;
    }

    public void setSummonedEntity(EntityType<?> entity) {
        this.summonedEntity = entity;
    }

    public void setSummonedEntityData(Optional<CompoundTag> data) {
        this.summonedEntityData = data;
    }


    public float getScaledProgress() {
        float entitySize = 1f;
        return maxProgress != 0 && progress != 0 ? progress * entitySize / maxProgress : 0;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new SummoningBlockMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.utility.summoning_block");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {

        inventory.serialize(output.child("inventory"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {

        inventory.deserialize(input.childOrEmpty("inventory"));
        maxProgress = input.getIntOr("maxProgress", 100);
        progress = input.getIntOr("progress", 0);

        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
    }
}
