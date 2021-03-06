package net.dirtcraft.dirtcommons.config;

import net.dirtcraft.dirtcommons.text.TextUtil;
import net.minecraft.util.text.Color;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ColorSerializer implements TypeSerializer<Color> {

    @Override
    public Color deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return TextUtil.parseRgb(node.getString());
    }

    @Override
    public void serialize(Type type, @Nullable Color obj, ConfigurationNode node) throws SerializationException {
        node.set(String.class, String.format("%06X", obj.value));
    }
}
