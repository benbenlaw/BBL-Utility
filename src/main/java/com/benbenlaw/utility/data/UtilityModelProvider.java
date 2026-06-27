package com.benbenlaw.utility.data;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.item.UtilityItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class UtilityModelProvider extends ModelProvider {

    public UtilityModelProvider(PackOutput output) {
        super(output, Utility.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        //Items
        itemModels.generateFlatItem(UtilityItems.CROOK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.SAPLING_GROWER.get(), ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        itemModels.generateFlatItem(UtilityItems.ANIMAL_NET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.DEATH_STONE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.FLOATER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.MINI_COAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.MINI_CHARCOAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.WOODEN_SHEARS.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(UtilityItems.LEAFY_STRING.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.SOAKED_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.LOG_SHEET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UtilityItems.ENDER_PEARL_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);

        //Blocks
        blockModels.createTrivialCube(UtilityBlocks.ENDER_ORE.get());
        blockModels.createTrivialCube(UtilityBlocks.DEEPSLATE_ENDER_ORE.get());

        //Machines
        //createMachineBlock(UtilityBlocks.DRYING_TABLE.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.BLOCK_PLACER.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.BLOCK_BREAKER.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.RESOURCE_GENERATOR.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.FLUID_PLACER.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.FLUID_COLLECTOR.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.FLUID_GENERATOR.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.ITEM_REPAIRER.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.REDSTONE_CLOCK.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.ITEM_COLLECTOR.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.SUMMONING_BLOCK.get(), blockModels.blockStateOutput, blockModels.modelOutput);
        createMachineBlock(UtilityBlocks.COMPACTOR.get(), blockModels.blockStateOutput, blockModels.modelOutput);


    }

    //This is a great method for any SyncableBlocks that we use in the future in either Utility or other mods
    public void createMachineBlock(Block block, Consumer<BlockModelDefinitionGenerator> blockStateOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
        TextureMapping idleTextureMapping = (new TextureMapping()).put(TextureSlot.TOP, new Material(Utility.identifier("block/machine_top"))).put(TextureSlot.SIDE, new Material(Utility.identifier("block/machine_side_idle"))).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front"));
        TextureMapping workingTextureMapping = (new TextureMapping()).put(TextureSlot.TOP, new Material(Utility.identifier("block/machine_top"))).put(TextureSlot.SIDE, new Material(Utility.identifier("block/machine_side_working"))).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front"));

        MultiVariant multivariant = plainVariant(ModelTemplates.CUBE_ORIENTABLE.create(block, idleTextureMapping, modelOutput));
        MultiVariant multivariant1 = plainVariant(ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(block, idleTextureMapping, modelOutput));

        MultiVariant workingVariant = plainVariant(ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_working", workingTextureMapping, modelOutput));
        MultiVariant workingVariant1 = plainVariant(ModelTemplates.CUBE_ORIENTABLE_VERTICAL.createWithSuffix(block, "_working", workingTextureMapping, modelOutput));

        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.FACING, SyncableBlock.RUNNING)
                                        .select(Direction.DOWN, false, multivariant1.with(X_ROT_180))
                                        .select(Direction.UP, false, multivariant1)
                                        .select(Direction.NORTH, false, multivariant)
                                        .select(Direction.EAST, false, multivariant.with(Y_ROT_90))
                                        .select(Direction.SOUTH,false, multivariant.with(Y_ROT_180))
                                        .select(Direction.WEST,false, multivariant.with(Y_ROT_270))
                                        .select(Direction.DOWN, true, workingVariant1.with(X_ROT_180))
                                        .select(Direction.UP, true, workingVariant1)
                                        .select(Direction.NORTH, true, workingVariant)
                                        .select(Direction.EAST, true, workingVariant.with(Y_ROT_90))
                                        .select(Direction.SOUTH,true, workingVariant.with(Y_ROT_180))
                                        .select(Direction.WEST,true, workingVariant.with(Y_ROT_270))));

    }


    @Override
    protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return UtilityBlocks.BLOCKS.getEntries().stream().filter(x ->
                !x.is(UtilityBlocks.DRYING_TABLE.getId()) &&
                !x.is(UtilityBlocks.CLICKER.getId())
        );
    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return UtilityItems.ITEMS.getEntries().stream();
    }

    @Override
    public @NotNull String getName() {
        return Utility.MOD_ID + " Models";
    }
}
