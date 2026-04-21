package com.benbenlaw.utility.network.packets;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.ItemCollectorBlockEntity;
import com.benbenlaw.utility.block.entity.SummoningBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.Objects;
import java.util.Optional;

public record SyncEntitySummoningBlockPacket(BlockPos pos, EntityType<?> entityType, CompoundTag entityData) implements CustomPacketPayload {

    public static final Type<SyncEntitySummoningBlockPacket> TYPE = new Type<>(Utility.identifier("sync_entity_summoning_block"));

    public static final IPayloadHandler<SyncEntitySummoningBlockPacket> HANDLER = (packet, context) -> {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.getBlockEntity(packet.pos) instanceof SummoningBlockEntity summoningBlockEntity) {
                summoningBlockEntity.setSummonedEntity(packet.entityType);
                summoningBlockEntity.setSummonedEntityData(Optional.ofNullable(packet.entityData));
            }
        });
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEntitySummoningBlockPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncEntitySummoningBlockPacket::pos,
            ByteBufCodecs.registry(Registries.ENTITY_TYPE), SyncEntitySummoningBlockPacket::entityType,
            ByteBufCodecs.COMPOUND_TAG, SyncEntitySummoningBlockPacket::entityData,
            SyncEntitySummoningBlockPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}