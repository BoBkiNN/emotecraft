package io.github.kosmx.emotes.arch.gui.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.kosmx.playerAnim.core.util.MathHelper;
import dev.kosmx.playerAnim.core.util.Pair;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class EmoteListWidget extends ObjectSelectionList<EmoteListWidget.EmoteEntry> {
    protected List<EmoteEntry> emotes = new ArrayList<>();
    private boolean compactMode;

    public EmoteListWidget(Minecraft minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
        this.centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
        if (this.compactMode) {
            return this.width;
        }

        return this.width / 2;
    }

    @Override
    protected int getScrollbarPosition() {
        if (!this.compactMode) {
            return super.getScrollbarPosition();
        }

        return getX() + getRowWidth() - SCROLLBAR_WIDTH;
    }

    @Override
    protected void renderSelection(@NotNull GuiGraphics guiGraphics, int i, int j, int k, int l, int m) {
        if (this.compactMode && scrollbarVisible()) {
            int o = getRowLeft() - 2;
            int p = getRight() - 6 - 1;
            int q = i - 2;
            int r = i + k + 2;
            guiGraphics.fill(o, q, p, r, l);
            guiGraphics.fill(o + 1, q + 1, p - 1, r - 1, m);
        } else {
            super.renderSelection(guiGraphics, i, j, k, l, m);
        }
    }

    public void setEmotes(Iterable<EmoteHolder> list, boolean showInvalid){
        this.emotes.clear();
        for (EmoteHolder emoteHolder : list) {
            this.emotes.add(new EmoteEntry(emoteHolder));
        }
        if (showInvalid) {
            for (EmoteHolder emoteHolder : getEmptyEmotes()) {
                this.emotes.add(new EmoteEntry(emoteHolder));
            }
        }
        this.emotes.sort(Comparator.comparing(o -> o.emote.name.getString().toLowerCase()));
        filter(() -> "");
    }

    public void filter(Supplier<String> string){
        clearEntries();
        for(EmoteEntry emote : this.emotes) {
            if (emote.emote.name.getString().toLowerCase().contains(string.get()) || emote.emote.description.getString().toLowerCase().contains(string.get()) || emote.emote.author.getString().toLowerCase().equals(string.get())){
                this.addEntry(emote);
            }
        }
        this.setScrollAmount(0);
    }

    public Iterable<EmoteHolder> getEmptyEmotes(){
        Collection<EmoteHolder> empties = new LinkedList<>();
        for(Pair<UUID, InputConstants.Key> pair : ((ClientConfig) EmoteInstance.config).emoteKeyMap){
            if(!EmoteHolder.list.containsKey(pair.getLeft())){
                empties.add(new EmoteHolder.Empty(pair.getLeft()));
            }
        }
        return empties;
    }

    public class EmoteEntry extends ObjectSelectionList.Entry<EmoteEntry> {
        public final EmoteHolder emote;

        public EmoteEntry(EmoteHolder emote) {
            this.emote = emote;
        }

        @Override
        public void render(@NotNull GuiGraphics matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (hovered) {
                matrices.fill(x - 2, y - 1, x + entryWidth - 4, y + entryHeight + 1, MathHelper.colorHelper(66, 66, 66, 128));
            }
            matrices.drawString(minecraft.font, this.emote.name, x + 38, y + 1, 16777215);
            matrices.drawString(minecraft.font, this.emote.description, x + 38, y + 12, 8421504);
            if(!this.emote.author.getString().isEmpty()) {
                Component text = Component.translatable("emotecraft.emote.author")
                        .withStyle(ChatFormatting.GOLD)
                        .append(this.emote.author);

                matrices.drawString(minecraft.font, text, x + 38, y + 23, 8421504);
            }

            ResourceLocation texture = this.emote.getIconIdentifier();
            if (texture != null){
                RenderSystem.enableBlend();
                matrices.blit(texture, x, y, 32, 32, 0, 0, 256, 256, 256, 256);
                RenderSystem.disableBlend();
            }
        }

        public EmoteHolder getEmote() {
            return this.emote;
        }

        @Override
        public @NotNull Component getNarration() {
            return this.emote.name;
        }
    }

    public void setCompactMode(boolean compactMode) {
        this.compactMode = compactMode;
    }
}
