package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.api.proxy.INetworkInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public record EmotePacketPayload(@NotNull CustomPacketPayload.Type<?> id, @NotNull ByteBuffer bytes) implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return id;
    }

    public byte[] unwrapBytes() {
        return INetworkInstance.safeGetBytesFromBuffer(bytes());
    }

    public static @NotNull CustomPacketPayload createPacket(@NotNull CustomPacketPayload.Type<?> id, @NotNull ByteBuffer bytes) {
        return new EmotePacketPayload(id, bytes);
    }

    public static @NotNull CustomPacketPayload playPacket(@NotNull ByteBuffer bytes) {
        return createPacket(NetworkPlatformTools.EMOTE_CHANNEL_ID, bytes);
    }

    public static @NotNull CustomPacketPayload streamPacket(@NotNull ByteBuffer bytes) {
        return createPacket(NetworkPlatformTools.STREAM_CHANNEL_ID, bytes);
    }

    public static @NotNull CustomPacketPayload geyserPacket(@NotNull ByteBuffer bytes) {
        return createPacket(NetworkPlatformTools.GEYSER_CHANNEL_ID, bytes);
    }

    @NotNull
    public static StreamCodec<FriendlyByteBuf, EmotePacketPayload> reader(@NotNull CustomPacketPayload.Type<?> channel) {
        return new StreamCodec<>() {
            @Override
            public @NotNull EmotePacketPayload decode(@NotNull FriendlyByteBuf buf) {
                return new EmotePacketPayload(channel, PlatformTools.rewrap(buf));
            }

            @Override
            public void encode(@NotNull FriendlyByteBuf buf, @NotNull EmotePacketPayload payload) {
                buf.writeBytes(payload.bytes());
            }
        };
    }

    public static final StreamCodec<FriendlyByteBuf, EmotePacketPayload> EMOTE_CHANNEL_READER = reader(NetworkPlatformTools.EMOTE_CHANNEL_ID);
    public static final StreamCodec<FriendlyByteBuf, EmotePacketPayload> STREAM_CHANNEL_READER = reader(NetworkPlatformTools.STREAM_CHANNEL_ID);
    public static final StreamCodec<FriendlyByteBuf, EmotePacketPayload> GEYSER_CHANNEL_READER = reader(NetworkPlatformTools.GEYSER_CHANNEL_ID);
}
