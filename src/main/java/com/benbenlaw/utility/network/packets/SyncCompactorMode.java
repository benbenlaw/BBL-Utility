package com.benbenlaw.utility.network.packets;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.CompactorBlockEntity;
import com.benbenlaw.utility.block.entity.SummoningBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.Optional;

public record SyncCompactorMode(BlockPos pos) implements CustomPacketPayload {

    public static final Type<SyncCompactorMode> TYPE = new Type<>(Utility.identifier("sync_compactor_mode"));

    public static final IPayloadHandler<SyncCompactorMode> HANDLER = (packet, context) -> {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.getBlockEntity(packet.pos) instanceof CompactorBlockEntity compactorBlockEntity) {
                compactorBlockEntity.toggleCraftingMode();
            }
        });
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCompactorMode> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncCompactorMode::pos,
            SyncCompactorMode::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}