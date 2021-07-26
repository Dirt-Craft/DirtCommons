package net.dirtcraft.dirtcommons.api;

import net.dirtcraft.dirtcommons.lib.AbstractDirtCommonsPlugin;
import net.dirtcraft.dirtcommons.lib.config.ConfigManagerImpl;

public interface Commons {
    static Commons getInstance(){
        return AbstractDirtCommonsPlugin.INSTANCE;
    }

    String getServerName();

    ConfigManagerImpl getConfigManager();
}
