package net.dirtcraft.dirtcommons;

import net.dirtcraft.dirtcommons.permission.Permissions;
import net.dirtcraft.dirtcommons.threads.ThreadManager;
import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.dirtcraft.dirtcommons.user.PlayerList;

public abstract class DirtCommons<T extends CommonsPlayer<?, ?, ?>, U> {
    protected static DirtCommons INSTANCE = null;

    public abstract Permissions getPermissionHelper();

    public abstract ThreadManager getScheduler();

    public abstract PlayerList<T, U> getPlayers();

    public static DirtCommons getInstance() {
        return INSTANCE;
    }
}
