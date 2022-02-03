package net.dirtcraft.dirtcommons.config;

import io.leangen.geantyref.TypeToken;
import net.dirtcraft.dirtcommons.configuration.ConfigurationHocon;
import net.dirtcraft.dirtcommons.util.Cycler;
import net.minecraft.item.Item;
import net.minecraft.util.text.Color;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.util.HashSet;

public class ForgeHocon<T> extends ConfigurationHocon<T> {
    public static final ItemSerializer ITEM = new ItemSerializer();
    public static final ColorSerializer COLOR = new ColorSerializer();
    public static final WorldSerializer WORLD = new WorldSerializer();
    public ForgeHocon(Path folder, String file, TypeToken<T> token, T instance) {
        super(folder, file, token, instance);
    }

    @Override
    protected void addCustomSerializers(TypeSerializerCollection.Builder serializers) {
        super.addCustomSerializers(serializers);
        serializers.register(Item.class, ITEM);
        serializers.register(Color.class, COLOR);
    }
}
