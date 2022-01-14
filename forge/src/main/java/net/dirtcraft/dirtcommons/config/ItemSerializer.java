package net.dirtcraft.commons.config;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ItemSerializer implements TypeSerializer<Item> {

    @Override
    public Item deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.getString() == null) return Items.AIR;
        return GameRegistry.findRegistry(Item.class).getValue(ResourceLocation.of(node.getString(), ':'));
    }

    @Override
    public void serialize(Type type, @Nullable Item obj, ConfigurationNode node) throws SerializationException {
        if (obj == null || obj.getRegistryName() == null) node.set(String.class, null);
        else node.set(String.class, obj.getRegistryName().toString());
    }
}
