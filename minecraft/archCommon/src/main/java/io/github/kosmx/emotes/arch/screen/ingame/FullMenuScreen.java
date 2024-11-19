package io.github.kosmx.emotes.arch.screen.ingame;

import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import io.github.kosmx.emotes.arch.screen.EmoteMenu;
import io.github.kosmx.emotes.arch.screen.components.EmoteSubScreen;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class FullMenuScreen extends EmoteSubScreen {
    protected static final Component TITLE = Component.translatable("emotecraft.emotelist");
    protected static final Component CONFIG = Component.translatable("emotecraft.config");

    public FullMenuScreen(Screen parent) {
        super(TITLE, parent);
    }

    @Override
    protected void addOptions() {
        this.list.setEmotes(EmoteHolder.list, false);
    }

    @Override
    protected void addFooter() {
        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(Button.DEFAULT_SPACING));

        linearLayout.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .build()
        );
        linearLayout.addChild(Button.builder(FullMenuScreen.CONFIG, button -> this.minecraft.setScreen(new EmoteMenu(this)))
                .build()
        );
    }

    @Override
    protected void onPressed(EmoteListWidget.EmoteEntry selected) {
        if (selected != null) {
            ClientEmotePlay.clientStartLocalEmote(selected.getEmote());
            // this.minecraft.setScreen(null); In my opinion, it's inconvenient
        }
    }

    @Override
    protected void renderBlurredBackground(float f) {
        if (this.list != null && this.list.getSelected() != null) {
            return;
        }

        super.renderBlurredBackground(f);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
