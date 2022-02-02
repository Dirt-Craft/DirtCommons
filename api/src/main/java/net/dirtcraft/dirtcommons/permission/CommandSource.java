package net.dirtcraft.dirtcommons.permission;

import net.dirtcraft.dirtcommons.chat.MessageReceiver;

public interface CommandSource<T> extends MessageReceiver<T>, Permissible{
}
