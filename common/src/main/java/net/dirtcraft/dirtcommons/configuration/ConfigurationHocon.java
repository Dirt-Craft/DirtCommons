package net.dirtcraft.dirtcommons.configuration;

import io.leangen.geantyref.TypeToken;
import net.dirtcraft.dirtcommons.Commons;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ConfigurationHocon<T> {
    private final static Executor threadPool = Commons.getInstance().getScheduler().getAsyncExecutor();
    private final AtomicBoolean isDirty = new AtomicBoolean();
    private final AtomicBoolean saving = new AtomicBoolean();
    private final HoconConfigurationLoader loader;
    private final TypeToken<T> token;
    private ConfigurationNode node;
    protected T config;

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public ConfigurationHocon(Path folder, String file, TypeToken<T> token, T instance) {
        this.token = token;
        final File loc = folder.resolve(file + ".hocon").toFile();
        folder.toFile().mkdirs();
        loader = HoconConfigurationLoader.builder()
                .file(loc)
                .build();
        try{
            node = loader.load(loader.defaultOptions().shouldCopyDefaults(true));
            config = node.get(token, instance);
            loader.save(node);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            node = loader.load(loader.defaultOptions().shouldCopyDefaults(true));
            config = node.get(token, config);
            loader.save(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(){
        isDirty.set(true);
        if (saving.getAndSet(true)) return;
        CompletableFuture.supplyAsync(()->{
            while (true) {
                try {
                    node.set(token, config);
                    loader.save(node);
                    isDirty.set(false);
                    Thread.sleep(5000);
                    if (!isDirty.get()){
                        saving.set(false);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    saving.set(false);
                    return false;
                }
            }
        }, threadPool);
    }
}