package com.benbenlaw.utility.network.packets;

import com.benbenlaw.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record SyncWhitelistMode(BlockPos pos, boolean whitelist) implements CustomPacketPayload {

    public static final Type<SyncWhitelistMode> TYPE = new Type<>(Utility.rl("sync_whitelist_mode"));

    public static final IPayloadHandler<SyncWhitelistMode> HANDLER = (packet, context) -> {
        BlockEntity entity = context.player().level().getBlockEntity(packet.pos);

        if (entity instanceof IWhitelistMode mode) {
            mode.setWhitelistMode(packet.whitelist);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWhitelistMode> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncWhitelistMode::pos,
            ByteBufCodecs.BOOL, SyncWhitelistMode::whitelist,
            SyncWhitelistMode::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
