package io.github.kosmx.emotes.arch.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.gui.screen.ConfigScreen;
import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import io.github.kosmx.emotes.arch.screen.components.EmoteSubScreen;
import io.github.kosmx.emotes.arch.screen.utils.EmoteListener;
import io.github.kosmx.emotes.arch.screen.widget.AbstractFastChooseWidget;
import io.github.kosmx.emotes.arch.screen.widget.IChooseWheel;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.config.ClientConfig;
import io.github.kosmx.emotes.main.config.ClientSerializer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

public class EmoteMenu extends EmoteSubScreen {
    private static final Component TITLE = Component.translatable("emotecraft.menu");

    public static final Component OPEN_FOLDER = Component.translatable("emotecraft.openFolder");
    private static final Component OPTIONS = Component.translatable("emotecraft.options.options");

    public static final Component RESET = Component.translatable("controls.reset");

    private static final Component KEYBIND = Component.translatable("emotecraft.options.keybind");
    private static final Component FASTMENU = Component.translatable("emotecraft.options.fastmenu");
    private static final Component FASTMENU2 = Component.translatable("emotecraft.options.fastmenu2");
    private static final Component FASTMENU3 = Component.translatable("emotecraft.options.fastmenu3");

    private static final Component SURE = Component.translatable("emotecraft.sure");
    private static final Component SURE2 = Component.translatable("emotecraft.sure2");

    private static final Component RESET_ONE = Component.translatable("controls.reset");
    private static final Component RESET_ALL = Component.translatable("controls.resetAll");

    private static final Component RESET_ALL_TITLE = Component.translatable("emotecraft.resetAllKeys.title");
    private static final Component RESET_ALL_MSG = Component.translatable("emotecraft.resetAllKeys.message");

    public final EmoteListener watcher;

    public long activeKeyTime;
    private Button setKeyButton;
    private Button resetButton;
    private boolean resetOnlySelected;

    protected FastChooseWidget fastChoose;

    public EmoteMenu(Screen parent) {
        super(EmoteMenu.TITLE, parent);
        this.watcher = new EmoteListener(EmoteInstance.instance.getExternalEmoteDir().toPath());
        this.watcher.load(this::addOptions);
    }

    @Override
    protected void addContents() {
        LinearLayout linearLayout = this.layout.addToContents(LinearLayout.horizontal().spacing(Button.DEFAULT_SPACING));

        this.list = linearLayout.addChild(newEmoteListWidget());
        addOptions();

        GridLayout gridLayout = linearLayout.addChild(new GridLayout());
        gridLayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(2);

        rowHelper.addChild(new StringWidget(KEYBIND, this.font), 2);

        this.setKeyButton = rowHelper.addChild(Button.builder(InputConstants.UNKNOWN.getDisplayName(), button -> {
            if (this.list != null && this.list.getSelected() != null){
                this.activeKeyTime = 200;
            }
        }).width(Button.SMALL_WIDTH).build());
        this.setKeyButton.active = false;

        this.resetButton = rowHelper.addChild(Button.builder(RESET, this::resetKeyAction)
                .width(Button.SMALL_WIDTH)
                .build()
        );
        this.resetButton.active = false;

        rowHelper.addChild(new StringWidget(FASTMENU, this.font), 2,
                gridLayout.newCellSettings().paddingTop(Button.DEFAULT_HEIGHT)
        );
        rowHelper.addChild(new StringWidget(FASTMENU2, this.font), 2);
        rowHelper.addChild(new StringWidget(FASTMENU3, this.font), 2);

        this.fastChoose = rowHelper.addChild(new FastChooseWidget(0, 0, 0), 2);
    }

    @Override
    protected void addOptions() {
        if (this.list != null) {
            this.list.setEmotes(EmoteHolder.list, true);
        }
    }

    @Override
    protected void addFooter() {
        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(Button.DEFAULT_SPACING));

        linearLayout.addChild(Button.builder(EmoteMenu.OPEN_FOLDER, button -> PlatformTools.openExternalEmotesDir())
                .width(Button.SMALL_WIDTH)
                .build()
        );
        linearLayout.addChild(Button.builder(EmoteMenu.OPTIONS, button -> this.minecraft.setScreen(new ConfigScreen(this)))
                .width(Button.SMALL_WIDTH)
                .build()
        );
        linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .width(Button.SMALL_WIDTH)
                .build()
        );
    }

    private void resetKeyAction(Button button){
        if(resetOnlySelected) {
            if (this.list == null || this.list.getFocused() == null) return;
            ((ClientConfig)EmoteInstance.config).emoteKeyMap.removeL(this.list.getFocused().getEmote().getUuid());
            onPressed(this.list.getSelected());
        } else {
            this.minecraft.setScreen(new ConfirmScreen(aBoolean -> {
                if (aBoolean) {
                    ((ClientConfig) EmoteInstance.config).emoteKeyMap.clear(); //reset :D
                    onPressed(this.list.getSelected());
                }
                this.minecraft.setScreen(EmoteMenu.this);
                }, RESET_ALL_TITLE, RESET_ALL_MSG.copy().append(" (" + ((ClientConfig)EmoteInstance.config).emoteKeyMap.size() + ")")
            ));
        }
    }

    @Override
    protected void repositionElements() {
        if (this.fastChoose != null) {
            int x = Math.min(this.width / 4, (int) (this.height / 2.5)) - 7;
            this.fastChoose.setSize(x, x);
        }
        super.repositionElements();
        if (this.list != null) {
            this.list.setCompactMode(true);
            this.list.setWidth(this.width / 3);
            this.layout.arrangeElements();
        }
    }

    @Override
    protected void onPressed(EmoteListWidget.EmoteEntry selected) {
        this.setKeyButton.active = this.resetButton.active = selected != null;

        if (selected != null) {
            this.setKeyButton.setMessage(getKey(selected.getEmote().getUuid()).getDisplayName());
            this.resetOnlySelected = ((ClientConfig) EmoteInstance.config).emoteKeyMap.containsL(selected.getEmote().getUuid());
        } else {
            this.resetOnlySelected = false;
        }

        if (resetOnlySelected) {
            this.resetButton.active = true;
            this.resetButton.setMessage(RESET_ONE);
        } else {
            if (!((ClientConfig)EmoteInstance.config).emoteKeyMap.isEmpty()) {
                this.resetButton.active = true;
                this.resetButton.setMessage(RESET_ALL.copy().append(" (" + ((ClientConfig)EmoteInstance.config).emoteKeyMap.size() + ")"));
            } else {
                this.resetButton.active = false;
                this.resetButton.setMessage(RESET_ONE);
            }
        }
    }

    @Override
    public void tick(){
        if(activeKeyTime == 1){
            setFocused(null);
        }
        if(activeKeyTime != 0){
            activeKeyTime--;
        }
        if (this.watcher != null && this.watcher.isFilesChanged()){
            this.watcher.load(this::addOptions);
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if (this.activeKeyTime != 0 && this.list != null && this.list.getFocused() != null){
            return setKey(InputConstants.Type.MOUSE.getOrCreate(button));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean setKey(InputConstants.Key key){
        boolean bl = false;
        if (this.list != null && this.list.getFocused() != null) {
            bl = true;
            if (!applyKey(false, this.list.getFocused().getEmote(), key)) {
                this.minecraft.setScreen(new ConfirmScreen(choice -> {
                    if (choice) {
                        applyKey(true, this.list.getFocused().getEmote(), key);
                    }
                    this.minecraft.setScreen(this);
                }, SURE, SURE2));
            }
        }
        return bl;
    }

    private boolean applyKey(boolean force, EmoteHolder emote, InputConstants.Key key){
        boolean bl = true;
        for(EmoteHolder emoteHolder : EmoteHolder.list){
            if(! key.equals(InputConstants.UNKNOWN) && getKey(emoteHolder.getUuid()).equals(key)){
                bl = false;
                if(force){
                    //emoteHolder.keyBinding = TmpGetters.getDefaults().getUnknownKey();
                    ((ClientConfig)EmoteInstance.config).emoteKeyMap.removeL(emoteHolder.getUuid());
                }
            }
        }
        if(bl || force){
            ((ClientConfig)EmoteInstance.config).emoteKeyMap.put(emote.getUuid(), key);
            onPressed(this.list.getSelected());
        }
        this.activeKeyTime = 0;
        return bl;
    }

    @NotNull
    public static InputConstants.Key getKey(UUID emoteID) {
        InputConstants.Key key;
        if((key = ((ClientConfig)EmoteInstance.config).emoteKeyMap.getR(emoteID)) == null){
            return InputConstants.UNKNOWN;
        }
        return key;
    }

    @Override
    public void removed() {
        this.watcher.blockWhileLoading();
        super.removed();
        ClientSerializer.saveConfig();
        try {
            this.watcher.close();
        } catch (Throwable th) {
            EmoteInstance.instance.getLogger().log(Level.WARNING, "Failed to close watcher!", th);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mod){
        if (this.list != null && this.list.getFocused() != null && activeKeyTime != 0) {
            if (keyCode == 256) {
                return setKey(InputConstants.UNKNOWN);
            }
            else {
                return setKey(InputConstants.getKey(keyCode, scanCode));
            }
        }
        return super.keyPressed(keyCode, scanCode, mod);
    }

    protected class FastChooseWidget extends AbstractFastChooseWidget {
        public FastChooseWidget(int x, int y, int size) {
            super(x, y, size);
        }

        @Override
        protected boolean isValidClickButton(int button){
            return (button == 0 || button == 1) && activeKeyTime == 0;
        }

        @Override
        protected boolean onClick(IChooseWheel.IChooseElement element, int button){
            if(activeKeyTime != 0) return false;
            if(button == 1){
                element.clearEmote();
                return true;
            } else if (list != null && list.getFocused() != null) {
                element.setEmote(list.getFocused().getEmote());
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected boolean doHoverPart(IChooseWheel.IChooseElement part){
            return activeKeyTime == 0;
        }

        @Override
        protected boolean doesShowInvalid() {
            return true;
        }
    }
}
