package net.dirtcraft.commons.command;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;

import java.util.List;

public interface CommandElement<T> {
    String getName();
    default String getCommand(){
        return getName().replace(' ', '-').toLowerCase();
    }
    List<String> getSuggestions();
    void execute(CommandSource source, T obj, String args);
    void execute(CommandSource source, T obj);
    IFormattableTextComponent getText(T obj, String command, boolean canEdit, Color k, Color v);
}
