package io.github.kosmx.emotes.arch.network.client.fabric;

import io.github.kosmx.emotes.arch.network.EmotePacketPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class ClientNetworkImpl {
    public static boolean isServerChannelOpen(ResourceLocation id) {
        return ClientPlayNetworking.canSend(id);
    }

    public static @NotNull Packet<?> createServerboundPacket(@NotNull final CustomPacketPayload.Type<EmotePacketPayload> id, @NotNull ByteBuffer buf) {
        assert buf.hasRemaining();

        return ClientPlayNetworking.createC2SPacket(EmotePacketPayload.createPacket(id, buf));
    }
}
