package com.benbenlaw.utility.network;

import com.benbenlaw.core.network.packets.UpdateFilterFluidSlotsPacket;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.network.packets.SyncCompactorMode;
import com.benbenlaw.utility.network.packets.SyncEntitySummoningBlockPacket;
import com.benbenlaw.utility.network.packets.SyncItemCollectorPacket;
import com.benbenlaw.utility.network.packets.SyncRedstoneClockPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class UtilityNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(Utility.MOD_ID);

        registrar.playToServer(SyncRedstoneClockPacket.TYPE, SyncRedstoneClockPacket.STREAM_CODEC, SyncRedstoneClockPacket.HANDLER);
        registrar.playToServer(SyncItemCollectorPacket.TYPE, SyncItemCollectorPacket.STREAM_CODEC, SyncItemCollectorPacket.HANDLER);
        registrar.playToServer(SyncCompactorMode.TYPE, SyncCompactorMode.STREAM_CODEC, SyncCompactorMode.HANDLER);

        registrar.playToClient(SyncEntitySummoningBlockPacket.TYPE, SyncEntitySummoningBlockPacket.STREAM_CODEC, SyncEntitySummoningBlockPacket.HANDLER);
    }
}
