package com.benbenlaw.utility.network.packets;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.ItemCollectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record SyncItemCollectorPacket(BlockPos pos, int offsetXPos, int offsetYPos, int offsetZPos, int xSize, int ySize, int zSize) implements CustomPacketPayload {

    public static final Type<SyncItemCollectorPacket> TYPE = new Type<>(Utility.identifier("sync_item_collector"));

    public static final IPayloadHandler<SyncItemCollectorPacket> HANDLER = (packet, context) -> {

        ItemCollectorBlockEntity itemCollectorBlockEntity = (ItemCollectorBlockEntity) context.player().level().getBlockEntity(packet.pos);

        if (itemCollectorBlockEntity != null) {

            itemCollectorBlockEntity.setOffsetX(packet.offsetXPos);
            itemCollectorBlockEntity.setOffsetY(packet.offsetYPos);
            itemCollectorBlockEntity.setOffsetZ(packet.offsetZPos);
            itemCollectorBlockEntity.setSizeX(packet.xSize);
            itemCollectorBlockEntity.setSizeY(packet.ySize);
            itemCollectorBlockEntity.setSizeZ(packet.zSize);

            context.player().playSound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS.ordinal(), 1.0f);
        }
    };

        public static final StreamCodec<RegistryFriendlyByteBuf, SyncItemCollectorPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncItemCollectorPacket::pos,
            ByteBufCodecs.INT, SyncItemCollectorPacket::offsetXPos,
            ByteBufCodecs.INT, SyncItemCollectorPacket::offsetYPos,
            ByteBufCodecs.INT, SyncItemCollectorPacket::offsetZPos,
            ByteBufCodecs.INT, SyncItemCollectorPacket::xSize,
            ByteBufCodecs.INT, SyncItemCollectorPacket::ySize,
            ByteBufCodecs.INT, SyncItemCollectorPacket::zSize,
            SyncItemCollectorPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
