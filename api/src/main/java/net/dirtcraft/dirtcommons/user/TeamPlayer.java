package net.dirtcraft.dirtcommons.user;

import net.dirtcraft.dirtcommons.util.LegacyColors;

public interface TeamPlayer {
    boolean isGlowing();

    void setGlowing(boolean value);

    LegacyColors getColor();

    void setColor(LegacyColors color);
}
