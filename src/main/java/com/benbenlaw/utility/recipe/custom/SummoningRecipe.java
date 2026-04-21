package com.benbenlaw.utility.recipe.custom;

import com.benbenlaw.utility.block.entity.SummoningBlockEntity;
import com.benbenlaw.utility.recipe.SummoningRecipeInput;
import com.benbenlaw.utility.util.BlockTarget;
import com.benbenlaw.utility.util.BlockTargetCodec;
import com.benbenlaw.utility.util.TemperatureValues;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record SummoningRecipe(SizedIngredient input, BlockTarget belowBlock, EntityType<?> summonedEntity, Optional<CompoundTag> entityData, Optional<TemperatureValues> temperatureVariant) implements Recipe<SummoningRecipeInput> {

    public static final MapCodec<SummoningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SizedIngredient.NESTED_CODEC.fieldOf("input").forGetter(SummoningRecipe::input),
                    BlockTargetCodec.CODEC.fieldOf("below_block").forGetter(SummoningRecipe::belowBlock),
                    EntityType.CODEC.fieldOf("entity").forGetter(recipe -> recipe.summonedEntity),
                    CompoundTag.CODEC.optionalFieldOf("entity_data").forGetter(SummoningRecipe::entityData),
                    TemperatureValues.CODEC.optionalFieldOf("temperature_variant").forGetter(SummoningRecipe::temperatureVariant)
            ).apply(instance, SummoningRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SummoningRecipe> STREAM_CODEC = StreamCodec.of(
            SummoningRecipe::write, SummoningRecipe::read);

    public static final RecipeType<SummoningRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<SummoningRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static SummoningRecipe read(RegistryFriendlyByteBuf buffer) {
        SizedIngredient input = SizedIngredient.STREAM_CODEC.decode(buffer);

        boolean isTag = buffer.readBoolean();
        BlockTarget blockTarget;
        if (isTag) {
            Identifier tagId = buffer.readIdentifier();
            blockTarget = new BlockTarget.Tag(TagKey.create(Registries.BLOCK, tagId));
        } else {
            BlockState blockState = Block.stateById(buffer.readInt());
            blockTarget = new BlockTarget.Single(blockState);
        }

        EntityType<?> entityType = buffer.readById(BuiltInRegistries.ENTITY_TYPE::byId);
        Optional<CompoundTag> entityData = buffer.readOptional(RegistryFriendlyByteBuf::readNbt);
        Optional<TemperatureValues> temperatureVariant = buffer.readOptional(TemperatureValues::readFromBuffer);

        return new SummoningRecipe(input, blockTarget, entityType, entityData, temperatureVariant);
    }

    private static void write(RegistryFriendlyByteBuf buffer, SummoningRecipe recipe) {
        SizedIngredient.STREAM_CODEC.encode(buffer, recipe.input);

        if (recipe.belowBlock instanceof BlockTarget.Tag(TagKey<Block> tag)) {
            buffer.writeBoolean(true);
            buffer.writeIdentifier(tag.location());
        } else if (recipe.belowBlock instanceof BlockTarget.Single(BlockState blockState)) {
            buffer.writeBoolean(false);
            buffer.writeInt(Block.getId(blockState));
        }

        buffer.writeById(BuiltInRegistries.ENTITY_TYPE::getId, recipe.summonedEntity);
        buffer.writeOptional(recipe.entityData, RegistryFriendlyByteBuf::writeNbt);
        buffer.writeOptional(recipe.temperatureVariant, TemperatureValues::writeToBuffer);

    }

    @Override
    public boolean matches(@NotNull SummoningRecipeInput recipeInput, Level level) {
        //if (level.isClientSide()) return false;

        boolean isBelowBlockMatching = belowBlock.matches(recipeInput.getBelowBlock(), false);
        boolean tempCheck;

        if (temperatureVariant.isPresent()) {
            TemperatureValues tempValues = temperatureVariant.get();
            TemperatureValues currentTempValues = recipeInput.getTemperatureVariant();
            tempCheck = tempValues == currentTempValues;
        } else {
            tempCheck = true;
        }

        ItemStack stack = recipeInput.getItem(SummoningBlockEntity.INPUT_SLOT);
        if (stack.isEmpty()) return false;

        if (isBelowBlockMatching) {
            return input.test(stack) && tempCheck;
        }

        return false;
    }

    //Boiler Plate
    @Override
    public @NotNull ItemStack assemble(@NotNull SummoningRecipeInput recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<SummoningRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<SummoningRecipeInput>> getType() {
        return TYPE;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }
}
