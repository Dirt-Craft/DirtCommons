package net.dirtcraft.commons.text;

import net.dirtcraft.commons.text.Colors;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class Styles {

    public static final Style PADDING = as(Colors.DARK_RED).setStrikethrough(true);
    public static final char PADDING_CH = '-';

    public static Style clean(Color color) {
        return as(color)
                .withClickEvent(null)
                .withHoverEvent(null);
    }

    public static Style as(Color color) {
        return Style.EMPTY.withColor(color);
    }

    public static Style withHoverText(Color color, String text) {
        return Style.EMPTY
                .withColor(color)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(text)));
    }

    public static Style withHoverText(Color color, TextComponent text) {
        return Style.EMPTY
                .withColor(color)
                .withHoverEvent(text == null? null: new HoverEvent(HoverEvent.Action.SHOW_TEXT, text));
    }

    public static Style withClickSuggest(Color color, String cmd) {
        return Style.EMPTY
                .withColor(color)
                .withClickEvent(cmd == null? null: new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
    }

    public static Style withClickCmd(Color color, String cmd) {
        return Style.EMPTY
                .withColor(color)
                .withClickEvent(cmd == null? null: new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
    }

    public static Style withHoverAndSuggestCmd(Color color, String hover, String cmd) {
        return Style.EMPTY
                .withColor(color)
                .withHoverEvent(hover == null? null: new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hover)))
                .withClickEvent(cmd == null? null: new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));

    }

    public static Style withHoverAndSuggestCmd(Color color, TextComponent hover, String cmd) {
        return Style.EMPTY
                .withColor(color)
                .withHoverEvent(hover == null? null: new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                .withClickEvent(cmd == null? null: new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
    }

    public static Style withHoverAndClickCmd(Color color, String hover, String cmd) {
        return Style.EMPTY
                .withColor(color)
                .withHoverEvent(hover == null? null: new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hover)))
                .withClickEvent(cmd == null? null: new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

    }

    public static Style withHoverAndClickCmd(Color color, TextComponent hover, String cmd) {
        return Style.EMPTY
                .withColor(color)
                .withHoverEvent(hover == null? null: new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                .withClickEvent(cmd == null? null: new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
    }

    public static String padCenter(String text, int amount, char padding) {
        if ((amount = amount - text.length())<0) return text;
        String pre = String.format("%" + amount/2 + "s", "").replace(' ', padding);
        String post = (amount&1)==0? pre : pre + padding;
        return String.join("", pre, text, post);
    }
}
