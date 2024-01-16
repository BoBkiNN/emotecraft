package io.github.kosmx.emotes.arch.executor.types;

import com.google.gson.JsonElement;
import io.github.kosmx.emotes.inline.dataTypes.Text;
import io.github.kosmx.emotes.inline.dataTypes.other.EmotesTextFormatting;
import io.github.kosmx.emotes.inline.TmpGetters;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class TextImpl implements Text {

    final MutableComponent MCText;

    public TextImpl(MutableComponent mcText) {
        MCText = mcText;
    }

    @Override
    public String getString() {
        return this.MCText.getString().replaceAll("§.", "");
    }

    @Override
    public JsonElement toJsonTree() {
        return net.minecraft.network.chat.Component.Serializer.toJsonTree(this.MCText);
    }

    @Override
    public Text formatted(EmotesTextFormatting form) {
        return new TextImpl(this.MCText.withStyle(iFormatToFormat(form)));
    }

    @Override
    public Text append(Text text) {
        return new TextImpl(this.MCText.append(((TextImpl) text).MCText));
    }

    @Override
    public Text append(String text) {
        return append(TmpGetters.getDefaults().textFromString(text));
    }

    protected ChatFormatting iFormatToFormat(EmotesTextFormatting emotesTextFormatting){
        return ChatFormatting.getByCode(emotesTextFormatting.getCode());
    }

    public MutableComponent get(){
        return this.MCText;
    }

    @Override
    public Text copyIt(){
        return new TextImpl(MCText.copy());
    }
}
