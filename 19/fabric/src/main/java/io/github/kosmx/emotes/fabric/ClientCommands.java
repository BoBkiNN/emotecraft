package io.github.kosmx.emotes.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.UUIDMap;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

/**
 * Client-side commands, no permission verification, we're on the client
 */
public class ClientCommands implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("emotes-client")
                .then(literal("play")
                        .then(argument("emote", StringArgumentType.string()).suggests(new EmoteArgumentHelper())
                                .executes(ctx -> {
                                    if (!ClientEmotePlay.clientStartLocalEmote(EmoteArgumentHelper.getEmote(ctx, "emote"))) {
                                        throw new SimpleCommandExceptionType(Component.translatable("emotecraft.cant.override.forced")).create();
                                    }
                                    return 0;
                                })
                        )
                )
                .then(literal("stop")
                        .executes(ctx -> {
                                    if (ClientEmotePlay.isForcedEmote())
                                        throw new SimpleCommandExceptionType(Component.translatable("emotecraft.cant.override.forced")).create();
                                    ClientEmotePlay.clientStopLocalEmote();
                                    return 0;
                                }
                        )
                )
        );
    }

    private static class EmoteArgumentHelper implements SuggestionProvider<FabricClientCommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
            UUIDMap<EmoteHolder> emotes = EmoteHolder.list;

            List<String> suggestions = new LinkedList<>();
            for (var emote : emotes.values()) {
                if (!emote.name.getString().equals("")) {
                    String name = emote.name.getString();
                    if (name.contains(" ")) {
                        name = "\"" + name + "\"";
                    }
                    suggestions.add(name);
                } else {
                    suggestions.add(emote.getUuid().toString());
                }
            }

            return SharedSuggestionProvider.suggest(suggestions.toArray(String[]::new), builder);
        }

        public static KeyframeAnimation getEmote(CommandContext<FabricClientCommandSource> context, String argumentName) throws CommandSyntaxException {
            String id = StringArgumentType.getString(context, argumentName);
            var emotes = EmoteHolder.list;
            try {
                UUID emoteID = UUID.fromString(id);
                EmoteHolder emote = emotes.get(emoteID);
                if (emote == null) throw new SimpleCommandExceptionType(Component.literal("No emote with ID: " + emoteID)).create();
                return emote.getEmote();
            } catch(IllegalArgumentException ignore) {} //Not a UUID

            for (var emote : emotes.values()) {
                if (emote.name.getString().equals(id)) {
                    return emote.getEmote();
                }
            }
            throw new SimpleCommandExceptionType(Component.literal("Not emote with name: " + id)).create();
        }
    }


}