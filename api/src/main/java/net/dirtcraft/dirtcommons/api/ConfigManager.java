package net.dirtcraft.dirtcommons.api;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public interface ConfigManager {
    <T> T loadConfig(ConfigurationLoader<CommentedConfigurationNode> loader, TypeToken<T> type, T def);

    <T> void saveConfig(ConfigurationLoader<CommentedConfigurationNode> loader, TypeToken<T> type, T value);

    ConfigurationLoader<CommentedConfigurationNode> getLoader(String modId, String name);
}
