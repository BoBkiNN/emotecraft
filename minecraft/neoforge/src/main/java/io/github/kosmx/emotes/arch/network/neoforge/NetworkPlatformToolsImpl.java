package io.github.kosmx.emotes.arch.network.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class NetworkPlatformToolsImpl {
    public static boolean canSendPlay(ServerPlayer player, ResourceLocation channel) {
        return player.connection.hasChannel(channel);
    }

    public static boolean canSendConfig(ServerConfigurationPacketListenerImpl packetListener, ResourceLocation channel) {
        return packetListener.hasChannel(channel);
    }
}
