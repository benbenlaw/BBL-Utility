package com.benbenlaw.utility.item;

import com.benbenlaw.utility.Utility;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UtilityDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Utility.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ENTITY_TYPE =
            COMPONENTS.register("entity_type", () ->
                    DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> ENTITY_DATA =
            COMPONENTS.register("entity_data", () ->
                    DataComponentType.<CompoundTag>builder().persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> GLOBAL_POS =
            COMPONENTS.register("global_pos", () ->
                    DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

}
