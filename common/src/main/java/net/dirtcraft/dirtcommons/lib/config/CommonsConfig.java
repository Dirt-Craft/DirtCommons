package net.dirtcraft.dirtcommons.lib.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class CommonsConfig {
    @Setting("server-name") public static final String SERVER_NAME = "";
}
