package io.github.kosmx.emotes.fabric.network;

import io.github.kosmx.emotes.arch.network.NetworkPlatformTools;
import io.github.kosmx.emotes.arch.network.client.ClientNetwork;
import io.github.kosmx.emotes.executor.EmoteInstance;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.io.IOException;
import java.util.logging.Level;

public class ClientNetworkInstance {

    public static void init(){
        FabricIsBestYouAreRightKosmX.init(true);

        // Configuration

        ClientConfigurationNetworking.registerGlobalReceiver(NetworkPlatformTools.EMOTE_CHANNEL_ID, (buf, context) -> {
            try {
                ClientNetwork.INSTANCE.receiveConfigMessage(buf.bytes(), context.responseSender()::sendPacket);
            } catch (IOException e) {
                EmoteInstance.instance.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        });

        ClientConfigurationNetworking.registerGlobalReceiver(NetworkPlatformTools.STREAM_CHANNEL_ID, (buf, context) -> {
            try {
                ClientNetwork.INSTANCE.receiveStreamMessage(buf.bytes(), context.responseSender()::sendPacket);
            } catch (IOException e) {
                EmoteInstance.instance.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        });

        // Play

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientNetwork.INSTANCE.disconnect());

        ClientPlayNetworking.registerGlobalReceiver(NetworkPlatformTools.EMOTE_CHANNEL_ID,
                (buf, context) -> ClientNetwork.INSTANCE.receiveMessage(buf.unwrapBytes())
        );

        ClientPlayNetworking.registerGlobalReceiver(NetworkPlatformTools.STREAM_CHANNEL_ID, (buf, context) -> {
            try {
                ClientNetwork.INSTANCE.receiveStreamMessage(buf.bytes(), null);
            } catch (IOException e) {
                EmoteInstance.instance.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        });
    }
}
