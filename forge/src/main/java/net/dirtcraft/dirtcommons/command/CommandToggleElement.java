package net.dirtcraft.dirtcommons.command;

import net.dirtcraft.dirtcommons.util.Object2BooleanFunction;
import net.dirtcraft.dirtcommons.util.ObjectBooleanConsumer;
import net.dirtcraft.dirtcommons.text.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Arrays;
import java.util.List;

public class CommandToggleElement<T> implements CommandElement<T> {
    private static final TextComponent HOVER = new StringTextComponent("Click to toggle!");
    public static final String PADDING = " ";
    private final ObjectBooleanConsumer<T> onClick;
    private final Object2BooleanFunction<T> getState;
    private final String name;
    private final String hover;
    private final List<String> suggestions = Arrays.asList(
            "true",
            "false"
    );

    public CommandToggleElement(ObjectBooleanConsumer<T> onClick, Object2BooleanFunction<T> getState, String name, String hover) {
        this.onClick = onClick;
        this.getState = getState;
        this.name = name;
        this.hover = hover;
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
    public void execute(CommandSource player, T obj, String args){
        onClick.accept(obj, Boolean.getBoolean(args));
    }

    @Override
    public void execute(CommandSource source, T obj) {
        onClick.accept(obj, !getState.apply(obj));
    }

    @Override
    public IFormattableTextComponent getText(T obj, String command, boolean edit, Color key, Color v) {
        Style flip = Style.EMPTY.withColor(getState.apply(obj) ? TextFormatting.GREEN : TextFormatting.RED);
        if (edit) flip = flip.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HOVER))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        IFormattableTextComponent toggle = new StringTextComponent(": ")
                .append(new StringTextComponent(getState.apply(obj)? "Enabled\n":"Disabled\n").withStyle(flip));
        IFormattableTextComponent name = new StringTextComponent(this.name)
                .withStyle(Styles.as(key)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hover)))
                        .withClickEvent(null));
        return new StringTextComponent(PADDING)
                .append(name)
                .append(toggle);
    }
}
