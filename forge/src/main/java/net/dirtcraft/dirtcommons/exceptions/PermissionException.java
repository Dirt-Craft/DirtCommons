package net.dirtcraft.dirtcommons.exceptions;

import net.dirtcraft.dirtcommons.text.Colors;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class PermissionException extends CommandException {
    public PermissionException(String node) {
        super(new StringTextComponent("You do not have permission to execute this command!").withStyle(Style.EMPTY
                .withColor(Colors.RED)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("failed to resolve: " + node)))
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, node))));
    }
}
