package io.github.kosmx.emotes.arch.network.client.neoforge;

import io.github.kosmx.emotes.arch.network.EmotePacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

public class ClientNetworkImpl {
    public static boolean isServerChannelOpen(ResourceLocation id) {
        return Objects.requireNonNull(Minecraft.getInstance().getConnection()).hasChannel(id);
    }

    public static @NotNull Packet<?> createServerboundPacket(@NotNull final CustomPacketPayload.Type<EmotePacketPayload> id, @NotNull ByteBuffer buf) {
        assert buf.hasRemaining();

        return new ServerboundCustomPayloadPacket(EmotePacketPayload.createPacket(id, buf));
    }
}
