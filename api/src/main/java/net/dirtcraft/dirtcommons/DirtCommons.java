package net.dirtcraft.dirtcommons;

import net.dirtcraft.dirtcommons.permission.Permissions;
import net.dirtcraft.dirtcommons.threads.ThreadManager;

public abstract class DirtCommons {
    protected static DirtCommons INSTANCE = null;

    public abstract Permissions getPermissionHelper();

    public abstract ThreadManager getScheduler();

    public static DirtCommons getInstance() {
        return INSTANCE;
    }
}
