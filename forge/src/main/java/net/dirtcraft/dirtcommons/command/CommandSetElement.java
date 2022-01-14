package net.dirtcraft.commons.command;

import net.dirtcraft.commons.text.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.dirtcraft.commons.text.Colors.*;

public class CommandSetElement<T, S> implements CommandElement<T> {
    private static final TextComponent HOVER = new StringTextComponent("Click to edit!");
    public static final String PADDING = " ";
    private final BiConsumer<T, S> onClick;
    private final Function<T, String> serializer;
    private final Function<String, S> deserializer;
    private final String name;
    private final String hover;

    public CommandSetElement(BiConsumer<T, S> onClick, Function<T, String> serializer, Function<String, S> deserializer, String name, String hover) {
        this.onClick = onClick;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.name = name;
        this.hover = hover;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getSuggestions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void execute(CommandSource player, T obj, String args){
        onClick.accept(obj, deserializer.apply(args));
    }

    @Override
    public void execute(CommandSource source, T obj) {
        source.sendFailure(new StringTextComponent("Missing argument \"value\".").withStyle(Styles.as(RED)));
    }

    @Override
    public IFormattableTextComponent getText(T obj, String command, boolean link, Color key, Color v) {
        Style title = Styles.as(key);
        Style reason = Styles.as(v);
        if (link) {
            reason = reason
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HOVER))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command + " ..."));
            title = title
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hover)));
        }
        return new StringTextComponent(PADDING + name).withStyle(title)
                .append(new StringTextComponent(": ").withStyle(Styles.as(DARK_GREY)))
                .append(new StringTextComponent(serializer.apply(obj) + "\n").setStyle(reason));
    }
}
