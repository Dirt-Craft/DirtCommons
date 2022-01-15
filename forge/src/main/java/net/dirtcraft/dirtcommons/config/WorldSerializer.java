package net.dirtcraft.dirtcommons.config;

import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class WorldSerializer implements TypeSerializer<World> {

    @Override
    public World deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable World obj, ConfigurationNode node) throws SerializationException {

    }
}
