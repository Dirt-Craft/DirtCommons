package net.dirtcraft.dirtcommons.core.api;

import net.minecraft.util.text.IFormattableTextComponent;

public interface InternalPlayerData {

    IFormattableTextComponent getPrefix();

    void setPrefix(IFormattableTextComponent prefix);

    String fbi$getName();
}
