package net.dirtcraft.dirtcommons.lib;

import net.dirtcraft.dirtcommons.api.Commons;
import net.dirtcraft.dirtcommons.lib.config.CommonsConfig;
import net.dirtcraft.dirtcommons.lib.config.ConfigManagerImpl;

import java.nio.file.Path;

public abstract class AbstractDirtCommonsPlugin implements Commons {
    public static Commons INSTANCE;
    public static final String MODID = "dirtcommons";
    public ConfigManagerImpl configManager = new ConfigManagerImpl(getBaseConfigDirectory());

    public AbstractDirtCommonsPlugin(){
        INSTANCE = this;
    }

    public abstract Path getBaseConfigDirectory();

    @Override
    public String getServerName() {
        return CommonsConfig.SERVER_NAME;
    }

    @Override
    public ConfigManagerImpl getConfigManager() {
        return configManager;
    }
}
