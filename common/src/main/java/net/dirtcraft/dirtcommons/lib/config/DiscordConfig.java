package net.dirtcraft.dirtcommons.lib.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class DiscordConfig {
    @Setting("discord-bot-token") public static final String TOKEN = "";
}
