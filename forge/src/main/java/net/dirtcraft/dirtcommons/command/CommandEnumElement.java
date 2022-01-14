package net.dirtcraft.commons.command;

import net.dirtcraft.commons.text.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandEnumElement<T, S extends Enum<S>> implements CommandElement<T>{
    private static final TextComponent HOVER = new StringTextComponent("Click to toggle!");
    public static final String PADDING = " ";
    private final BiConsumer<T, S> onClick;
    private final Function<T, S> getState;
    private final String name;
    private final String hover;
    private List<String> suggestions;
    private final Class<S> clazz;

    public CommandEnumElement(Class<S> clazz, BiConsumer<T, S> onClick, Function<T, S> getState, String name, String hover) {
        this.onClick = onClick;
        this.getState = getState;
        this.name = name;
        this.hover = hover;
        this.clazz = clazz;
        try {
            suggestions = Arrays.stream(clazz.getEnumConstants())
                    .map(s->s.name().toLowerCase())
                    .collect(Collectors.toList());
        } catch (Exception ignored) {ignored.printStackTrace();}
        if (suggestions == null) suggestions = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public void execute(CommandSource source, T obj, String args) {
        onClick.accept(obj, Enum.valueOf(clazz, args.toUpperCase()));
    }

    @Override
    public void execute(CommandSource source, T obj) {
        int idx = suggestions.indexOf(getState.apply(obj).name().toLowerCase()) + 1;
        if (idx == suggestions.size()) idx = 0;
        onClick.accept(obj, Enum.valueOf(clazz, suggestions.get(idx).toUpperCase()));
    }

    @Override
    public IFormattableTextComponent getText(T obj, String command, boolean edit, Color key, Color v) {
        Style value = Styles.as(v);
        if (edit) value = value.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HOVER))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        IFormattableTextComponent toggle = new StringTextComponent(": ")
                .append(new StringTextComponent(getState.apply(obj).name()).withStyle(value));
        IFormattableTextComponent name = new StringTextComponent(this.name)
                .withStyle(Styles.as(key)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hover)))
                        .withClickEvent(null));
        return new StringTextComponent(PADDING)
                .append(name)
                .append(toggle)
                .append("\n");
    }
}
