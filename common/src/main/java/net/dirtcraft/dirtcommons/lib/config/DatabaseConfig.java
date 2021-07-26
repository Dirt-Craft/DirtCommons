package net.dirtcraft.dirtcommons.lib.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class DatabaseConfig {
    @Setting public static final String IP = "localhost";
    @Setting public static final int PORT = 3306;
    @Setting public static final String USER = "root";
    @Setting public static final String PASS = "root";
}
