package net.dirtcraft.dirtcommons.user;

import net.dirtcraft.dirtcommons.util.LegacyColors;

public interface TeamPlayer {
    boolean isUserGlowing();

    void setUserGlowing(boolean value);

    LegacyColors getColor();

    void setColor(LegacyColors color);
}
