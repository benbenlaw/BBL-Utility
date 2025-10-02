package com.benbenlaw.utility.network.packets;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.RedstoneClockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record SyncRedstoneClockPacket(BlockPos pos, int maxProgress, int onTime, int signalStrength) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncRedstoneClockPacket> TYPE = new CustomPacketPayload.Type<>(Utility.rl("sync_redstone_clock"));

    public static final IPayloadHandler<SyncRedstoneClockPacket> HANDLER = (packet, context) -> {

        RedstoneClockBlockEntity redstoneClockBlockEntity = (RedstoneClockBlockEntity) context.player().level().getBlockEntity(packet.pos);

        if (redstoneClockBlockEntity != null) {

            redstoneClockBlockEntity.setMaxProgress(packet.maxProgress);
            redstoneClockBlockEntity.setOnTime(packet.onTime);
            redstoneClockBlockEntity.setSignalStrength(packet.signalStrength);
            context.player().playSound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS.ordinal(), 1.0f);
        }
    };

        public static final StreamCodec<RegistryFriendlyByteBuf, SyncRedstoneClockPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncRedstoneClockPacket::pos,
            ByteBufCodecs.INT, SyncRedstoneClockPacket::maxProgress,
            ByteBufCodecs.INT, SyncRedstoneClockPacket::onTime,
            ByteBufCodecs.INT, SyncRedstoneClockPacket::signalStrength,
            SyncRedstoneClockPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
