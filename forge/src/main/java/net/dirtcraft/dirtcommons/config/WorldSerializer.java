package net.dirtcraft.dirtcommons.config;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class WorldSerializer implements TypeSerializer<RegistryKey<World>> {

    @Override
    public RegistryKey<World> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable RegistryKey<World> obj, ConfigurationNode node) throws SerializationException {

    }
}
