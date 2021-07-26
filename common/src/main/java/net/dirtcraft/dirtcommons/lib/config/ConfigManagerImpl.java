package net.dirtcraft.dirtcommons.lib.config;

import io.leangen.geantyref.TypeToken;
import net.dirtcraft.dirtcommons.api.ConfigManager;
import net.dirtcraft.dirtcommons.lib.AbstractDirtCommonsPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManagerImpl implements ConfigManager {
    private final Map<ConfigurationLoader<CommentedConfigurationNode>, CommentedConfigurationNode> nodeMap = new HashMap<>();
    private final ConfigurationLoader<CommentedConfigurationNode> commons  = getLoader(AbstractDirtCommonsPlugin.MODID, "config");
    private final ConfigurationLoader<CommentedConfigurationNode> database = getLoader(AbstractDirtCommonsPlugin.MODID, "database");
    private final ConfigurationLoader<CommentedConfigurationNode> discord  = getLoader(AbstractDirtCommonsPlugin.MODID, "discord");
    private final Path basePath;

    public ConfigManagerImpl(@NonNull Path basePath) {
        this.basePath = basePath;
        reloadConfigs();
    }

    @Override
    public <T> T loadConfig(ConfigurationLoader<CommentedConfigurationNode> loader, TypeToken<T> type, T def) {
        try {
            CommentedConfigurationNode node = loader.load();
            nodeMap.put(loader, node);
            def = node.get(type, def);
            loader.save(node);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return def;
    }

    @Override
    public <T> void saveConfig(ConfigurationLoader<CommentedConfigurationNode> loader, TypeToken<T> type, T value) {
        try {
            CommentedConfigurationNode node = nodeMap.getOrDefault(loader, loader.load());
            node.set(type, value);
            loader.save(node);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ConfigurationLoader<CommentedConfigurationNode> getLoader(String modId, String name) {
        return HoconConfigurationLoader.builder()
                .file(new File(basePath.resolve(modId).toFile(), name + ".hocon"))
                .build();
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public void reloadConfigs(){
        loadConfig(database, new TypeToken<DatabaseConfig>(){}, new DatabaseConfig());
        loadConfig(discord, new TypeToken<DiscordConfig>(){}, new DiscordConfig());
        loadConfig(commons, new TypeToken<CommonsConfig>(){}, new CommonsConfig());
    }
}
