package io.github.kosmx.emotes.arch.screen.components;

import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Like {@link OptionsSubScreen} but with emotes.
 * Use to create your list of emotes. (dima_dencep uses it)
 */
public abstract class EmoteSubScreen extends Screen {
    private static final Component SEARCH = Component.translatable("gui.recipebook.search_hint");

    protected Screen lastScreen;

    @Nullable
    protected EmoteListWidget list;
    protected HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    protected EmoteSubScreen(Component title, Screen lastScreen) {
        super(title);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.addTitle();
        this.addContents();
        this.addFooter();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void addTitle() {
        EditBox searchBox = this.layout.addToHeader(new EditBox(this.font, 0, 0, Button.BIG_WIDTH, Button.DEFAULT_HEIGHT, Component.empty()));
        searchBox.setHint(SEARCH);
        searchBox.setResponder((string) -> Objects.requireNonNull(this.list).filter(string::toLowerCase));
    }

    protected EmoteListWidget newEmoteListWidget() {
        return new EmoteListWidget(
                this.minecraft, width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 36
        ) {
            @Override
            public void setSelected(@Nullable EmoteListWidget.EmoteEntry entry) {
                super.setSelected(entry);
                onPressed(entry);
            }
        };
    }

    protected void addContents() {
        this.list = this.layout.addToContents(newEmoteListWidget());
        addOptions();
    }

    protected abstract void addOptions();

    protected void addFooter() {
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(200).build());
    }

    protected abstract void onPressed(@Nullable EmoteListWidget.EmoteEntry selected);

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}
