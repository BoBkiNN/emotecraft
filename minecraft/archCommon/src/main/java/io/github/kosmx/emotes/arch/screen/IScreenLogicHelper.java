package io.github.kosmx.emotes.arch.screen;

import io.github.kosmx.emotes.inline.dataTypes.Text;
import io.github.kosmx.emotes.inline.dataTypes.screen.IConfirmScreen;
import io.github.kosmx.emotes.inline.dataTypes.screen.widgets.IButton;
import io.github.kosmx.emotes.inline.dataTypes.screen.widgets.ITextInputWidget;

import java.util.function.Consumer;

public interface IScreenLogicHelper<MATRIX> extends IRenderHelper<MATRIX> {

    IButton newButton(int x, int y, int width, int heitht, Text msg, Consumer<IButton> pressAction);

    ITextInputWidget newTextInputWidget(int x, int y, int width, int height, Text title);

    IConfirmScreen createConfigScreen(Consumer<Boolean> consumer, Text title, Text text);

    void openExternalEmotesDir();
}
