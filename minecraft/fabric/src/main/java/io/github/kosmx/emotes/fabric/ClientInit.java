package io.github.kosmx.emotes.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kosmx.emotes.arch.ClientCommands;
import io.github.kosmx.emotes.arch.executor.ClientMethods;
import io.github.kosmx.emotes.arch.screen.ingame.FastMenuScreen;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.fabric.network.ClientNetworkInstance;
import io.github.kosmx.emotes.main.MainClientInit;
import io.github.kosmx.emotes.main.config.ClientConfig;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ClientInit implements ClientModInitializer {

    static KeyMapping openMenuKey;
    static KeyMapping stopEmote;
    static KeyMapping debugKey;
    static Consumer<Minecraft> keyBindingFunction;


    @Override
    public void onInitializeClient(){

        initKeyBinding();

        ClientNetworkInstance.init(); //init network

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientMethods.tick++;

            keyBindingFunction.accept(client);
        });

        ClientCommandRegistrationCallback.EVENT.register(ClientCommands::register);
    }

    private static void initKeyBinding(){
        openMenuKey = new KeyMapping("key.emotecraft.fastchoose", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.emotecraft.keybinding");
        KeyBindingHelper.registerKeyBinding(openMenuKey);

        stopEmote = new KeyMapping("key.emotecraft.stop", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.emotecraft.keybinding");
        KeyBindingHelper.registerKeyBinding(stopEmote);

        if(FabricLoader.getInstance().getGameDir().resolve("emote.json").toFile().isFile()){ //Secret feature//
            debugKey = new KeyMapping("key.emotecraft.debug", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O,       //I don't know why... just
                    "category.emotecraft.keybinding");
            KeyBindingHelper.registerKeyBinding(debugKey);
        }
        keyBindingFunction = client -> {

            if(openMenuKey.consumeClick()){
                if(((ClientConfig) EmoteInstance.config).alwaysOpenEmoteScreen.get() || Minecraft.getInstance().player == Minecraft.getInstance().getCameraEntity()){
                    Minecraft.getInstance().setScreen(new FastMenuScreen(null));
                }
            }
            if(stopEmote.consumeClick()){
                ClientEmotePlay.clientStopLocalEmote();
            }
            if(debugKey != null && debugKey.consumeClick()){
                MainClientInit.playDebugEmote();
            }
        };
    }
}
