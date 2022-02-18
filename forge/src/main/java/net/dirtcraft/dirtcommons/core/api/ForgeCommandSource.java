package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.permission.CommandSource;
import net.minecraft.util.text.ITextComponent;

public interface ForgeCommandSource extends CommandSource<ITextComponent>, ForgeMessageReceiver {
}
