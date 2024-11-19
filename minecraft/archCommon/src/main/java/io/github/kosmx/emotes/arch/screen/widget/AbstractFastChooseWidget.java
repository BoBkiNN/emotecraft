package io.github.kosmx.emotes.arch.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFastChooseWidget extends AbstractWidget {
    private IChooseWheel wheel;

    protected AbstractFastChooseWidget(int x, int y, int size) {
        super(x, y, size, size, Component.empty());
        this.wheel = IChooseWheel.getWheel(this);
    }

    protected void bind(IChooseWheel wheel) {
        this.wheel = wheel;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics matrices, int mouseX, int mouseY, float delta) {
        this.wheel.render(matrices, mouseX, mouseY, delta);
    }

    protected abstract boolean doHoverPart(IChooseWheel.IChooseElement part);

    protected abstract boolean isValidClickButton(int button);

    protected abstract boolean onClick(IChooseWheel.IChooseElement element, int button);  //What DO I want to do with this element? set or play.

    protected abstract boolean doesShowInvalid();

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.wheel.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.wheel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.wheel.isMouseOver(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
