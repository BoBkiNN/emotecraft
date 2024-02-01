package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.PacketConfig;
import io.github.kosmx.emotes.common.network.objects.NetData;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class ConfigTask implements ConfigurationTask {
    public static final ConfigurationTask.Type TYPE = new Type("emotecraft:config");

    @NotNull
    private final ServerConfigurationPacketListenerImpl handler;

    public ConfigTask(@NotNull ServerConfigurationPacketListenerImpl handler) {
        this.handler = handler;
    }

    @Override
    public void start(@NotNull Consumer<Packet<?>> consumer) {
        NetData configData = new EmotePacket.Builder().configureToConfigExchange(true).build().data;
        configData.versions.put(PacketConfig.SERVER_TRACK_EMOTE_PLAY, (byte)0x01); // track player state
        try {
            var bytes = new EmotePacket.Builder(configData).build().write();
            consumer.accept(new ClientboundCustomPayloadPacket(EmotePacketPayload.playPacket(bytes))); // Config init
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull Type type() {
        return TYPE;
    }
}
