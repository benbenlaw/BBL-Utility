package com.benbenlaw.utility.network.packets;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.entity.ClickerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record SyncClickerSettings(BlockPos pos, boolean toggleSneak) implements CustomPacketPayload {

    public static final Type<SyncClickerSettings> TYPE = new Type<>(Utility.identifier("sync_clicker_settings"));

    public static final IPayloadHandler<SyncClickerSettings> HANDLER = (packet, context) -> {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.getBlockEntity(packet.pos) instanceof ClickerBlockEntity clicker) {
                if (packet.toggleSneak) {
                    clicker.toggleSneaking();
                } else {
                    clicker.toggleClickType();
                }
            }
        });
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncClickerSettings> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncClickerSettings::pos,
            ByteBufCodecs.BOOL, SyncClickerSettings::toggleSneak,
            SyncClickerSettings::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
