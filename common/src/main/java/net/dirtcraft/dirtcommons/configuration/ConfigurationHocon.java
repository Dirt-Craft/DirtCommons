package net.dirtcraft.dirtcommons.configuration;

import io.leangen.geantyref.TypeToken;
import net.dirtcraft.dirtcommons.Commons;
import net.dirtcraft.dirtcommons.configuration.serializers.CyclerSerializer;
import net.dirtcraft.dirtcommons.configuration.serializers.HashSetSerializer;
import net.dirtcraft.dirtcommons.util.Cycler;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ConfigurationHocon<T> {
    private final static CyclerSerializer CYCLER = new CyclerSerializer();
    private final static HashSetSerializer HASH_SET = new HashSetSerializer();
    private final static TypeToken<Cycler<?>> CYCLER_TYPE = new TypeToken<Cycler<?>>(){};
    private final static TypeToken<HashSet<?>> HASH_SET_TYPE = new TypeToken<HashSet<?>>(){};
    private final static Executor threadPool = Commons.getInstance().getScheduler().getAsyncExecutor();
    private final AtomicBoolean isDirty = new AtomicBoolean();
    private final AtomicBoolean saving = new AtomicBoolean();
    protected final HoconConfigurationLoader loader;
    private final TypeToken<T> token;
    private ConfigurationNode node;
    protected T config;

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public ConfigurationHocon(Path folder, String file, TypeToken<T> token, T instance) {
        this.token = token;
        final File loc = folder.resolve(file + ".hocon").toFile();
        folder.toFile().mkdirs();
        TypeSerializerCollection.Builder serializer = TypeSerializerCollection.defaults()
                .childBuilder();
        this.addCustomSerializers(serializer);
        loader = HoconConfigurationLoader.builder()
                .defaultOptions(t->t.serializers(serializer.build()))
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

    protected void addCustomSerializers(TypeSerializerCollection.Builder serializers) {
        serializers.register(CYCLER_TYPE, CYCLER);
        serializers.register(HASH_SET_TYPE, HASH_SET);
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