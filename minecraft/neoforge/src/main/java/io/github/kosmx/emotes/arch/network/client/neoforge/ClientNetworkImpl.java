package io.github.kosmx.emotes.arch.network.client.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class ClientNetworkImpl {
    public static boolean isServerChannelOpen(ResourceLocation id) {
        return Objects.requireNonNull(Minecraft.getInstance().getConnection()).hasChannel(id);
    }
}
