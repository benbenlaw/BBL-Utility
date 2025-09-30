package com.benbenlaw.utility.network;

import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.network.packets.SyncWhitelistMode;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class UtilityNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(Utility.MOD_ID);

        registrar.playToServer(SyncWhitelistMode.TYPE, SyncWhitelistMode.STREAM_CODEC, SyncWhitelistMode.HANDLER);

    }
}
