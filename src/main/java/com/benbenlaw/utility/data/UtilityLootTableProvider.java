package com.benbenlaw.utility.data;

import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.item.UtilityItems;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class UtilityLootTableProvider extends VanillaBlockLoot {

    private final Set<Block> knownBlocks = new ReferenceOpenHashSet<>();

    public UtilityLootTableProvider(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    protected void generate() {

        this.createEnderOreDrops(UtilityBlocks.ENDER_ORE.get());
        this.createEnderOreDrops(UtilityBlocks.DEEPSLATE_ENDER_ORE.get());
        this.dropSelf(UtilityBlocks.DRYING_TABLE.get());
        this.dropSelf(UtilityBlocks.BLOCK_PLACER.get());
        this.dropSelf(UtilityBlocks.BLOCK_BREAKER.get());
        this.dropSelf(UtilityBlocks.RESOURCE_GENERATOR.get());
        this.dropSelf(UtilityBlocks.FLUID_COLLECTOR.get());
        this.dropSelf(UtilityBlocks.FLUID_PLACER.get());
    }

    protected void createEnderOreDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        LootTable.Builder table = this.createSilkTouchDispatchTable(
                block,
                this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(UtilityItems.ENDER_PEARL_FRAGMENT.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))
        );

        this.add(block, table);
    }

    @Override
    protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
        super.add(block, table);
        knownBlocks.add(block);
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }
}
