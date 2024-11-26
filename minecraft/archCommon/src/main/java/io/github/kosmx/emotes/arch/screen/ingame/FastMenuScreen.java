package io.github.kosmx.emotes.arch.screen.ingame;

import io.github.kosmx.emotes.arch.screen.widget.AbstractFastChooseWidget;
import io.github.kosmx.emotes.arch.screen.widget.IChooseWheel;
import io.github.kosmx.emotes.inline.TmpGetters;
import io.github.kosmx.emotes.main.network.ClientPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FastMenuScreen extends Screen {
    protected static final Component TITLE = Component.translatable("emotecraft.fastmenu");

    private static final Component WARN_NO_EMOTECRAFT = Component.translatable("emotecraft.no_server");
    private static final Component WARN_ONLY_PROXY = Component.translatable("emotecraft.only_proxy");

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    protected final Screen parent;

    protected FastMenuWidget fastMenu;

    public FastMenuScreen(Screen parent) {
        super(FastMenuScreen.TITLE);
        this.parent = parent;
    }

    @Override
    public void init() {
        if (ClientPacketManager.isRemoteAvailable()) {
            //this.layout.addTitleHeader(getTitle(), this.font); TODO Do we want this?
        } else if (ClientPacketManager.isAvailableProxy()) {
            this.layout.addTitleHeader(FastMenuScreen.WARN_ONLY_PROXY, this.font);
        } else {
            this.layout.addTitleHeader(FastMenuScreen.WARN_NO_EMOTECRAFT, this.font);
        }

        this.fastMenu = this.layout.addToContents(new FastMenuWidget(0, 0, 0));

        this.layout.addToFooter(Button.builder(FullMenuScreen.TITLE, button -> this.minecraft.setScreen(new FullMenuScreen(this)))
                .width(Button.SMALL_WIDTH)
                .build()
        );

        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.fastMenu != null) {
            int size = (int) Math.min(this.width * 0.8, this.height * 0.8);
            this.fastMenu.setSize(size, size);
        }
        this.layout.arrangeElements();
    }

    @Override
    protected void renderBlurredBackground(float f) {
        // no-op
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    protected static class FastMenuWidget extends AbstractFastChooseWidget {
        public FastMenuWidget(int x, int y, int size) {
            super(x, y, size);
        }

        @Override
        protected boolean doHoverPart(IChooseWheel.IChooseElement part){
            return part.hasEmote();
        }

        @Override
        protected boolean isValidClickButton(int button){
            return button == 0;
        }

        @Override
        protected boolean onClick(IChooseWheel.IChooseElement element, int button){
            if(element.getEmote() != null){
                boolean bl = element.getEmote().playEmote(TmpGetters.getClientMethods().getMainPlayer());
                Minecraft.getInstance().setScreen(null);
                return bl;
            }
            return false;
        }

        @Override
        protected boolean doesShowInvalid() {
            return false;
        }
    }
}
