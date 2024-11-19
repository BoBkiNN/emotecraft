package io.github.kosmx.emotes.fabric.network;

import io.github.kosmx.emotes.arch.network.EmotePacketPayload;
import io.github.kosmx.emotes.arch.network.NetworkPlatformTools;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class FabricIsBestYouAreRightKosmX {
    public static void init(boolean server) {
        if (server)
            PayloadTypeRegistry.configurationS2C().register(NetworkPlatformTools.EMOTE_CHANNEL_ID, EmotePacketPayload.EMOTE_CHANNEL_READER);
        else
            PayloadTypeRegistry.configurationC2S().register(NetworkPlatformTools.EMOTE_CHANNEL_ID, EmotePacketPayload.EMOTE_CHANNEL_READER);
        if (server)
            PayloadTypeRegistry.playS2C().register(NetworkPlatformTools.EMOTE_CHANNEL_ID, EmotePacketPayload.EMOTE_CHANNEL_READER);
        else
            PayloadTypeRegistry.playC2S().register(NetworkPlatformTools.EMOTE_CHANNEL_ID, EmotePacketPayload.EMOTE_CHANNEL_READER);

        if (server)
            PayloadTypeRegistry.configurationS2C().register(NetworkPlatformTools.STREAM_CHANNEL_ID, EmotePacketPayload.STREAM_CHANNEL_READER);
        else
            PayloadTypeRegistry.configurationC2S().register(NetworkPlatformTools.STREAM_CHANNEL_ID, EmotePacketPayload.STREAM_CHANNEL_READER);
        if (server)
            PayloadTypeRegistry.playS2C().register(NetworkPlatformTools.STREAM_CHANNEL_ID, EmotePacketPayload.STREAM_CHANNEL_READER);
        else
            PayloadTypeRegistry.playC2S().register(NetworkPlatformTools.STREAM_CHANNEL_ID, EmotePacketPayload.STREAM_CHANNEL_READER);

        if (server)
            PayloadTypeRegistry.configurationS2C().register(NetworkPlatformTools.GEYSER_CHANNEL_ID, EmotePacketPayload.GEYSER_CHANNEL_READER);
        else
            PayloadTypeRegistry.configurationC2S().register(NetworkPlatformTools.GEYSER_CHANNEL_ID, EmotePacketPayload.GEYSER_CHANNEL_READER);
        if (server)
            PayloadTypeRegistry.playS2C().register(NetworkPlatformTools.GEYSER_CHANNEL_ID, EmotePacketPayload.GEYSER_CHANNEL_READER);
        else
            PayloadTypeRegistry.playC2S().register(NetworkPlatformTools.GEYSER_CHANNEL_ID, EmotePacketPayload.GEYSER_CHANNEL_READER);
    }
}
