package net.dirtcraft.commons.configuration.serializers;

import net.dirtcraft.commons.util.Cycler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.util.CheckedConsumer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CyclerSerializer implements TypeSerializer<Cycler<?>> {
    protected Type elementType(final Type containerType) throws SerializationException {
        if (!(containerType instanceof ParameterizedType)) {
            throw new SerializationException("Raw types are not supported for collections");
        }
        return ((ParameterizedType) containerType).getActualTypeArguments()[0];
    }

    @Override
    public Cycler<?> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final Type entryType = elementType(type);
        final @Nullable TypeSerializer<?> entrySerial = node.options().serializers().get(entryType);
        if (entrySerial == null) {
            throw new SerializationException(node, entryType, "No applicable type serializer for type");
        }

        final List<? extends ConfigurationNode> values = node.childrenList();
        final Object[] varArgs = new Object[values.size() - 1];
        for (int i = 0; i < values.size(); ++i) {
            try {
                varArgs[i] = entrySerial.deserialize(entryType, values.get(i));
            } catch (final SerializationException ex) {
                int finalI = i;
                ex.initPath(()->values.get(finalI).path());
                throw ex;
            }
        }
        return Cycler.get(varArgs);
    }

    @Override
    public void serialize(Type type, @Nullable Cycler<?> obj, ConfigurationNode node) throws SerializationException {
        final Type entryType = elementType(type);
        final @Nullable TypeSerializer entrySerial = node.options().serializers().get(entryType);
        if (entrySerial == null) {
            throw new SerializationException(node, entryType, "No applicable type serializer for type");
        }

        node.raw(Collections.emptyList());
        if (obj != null) {
            forEachElement(obj.asList(), el -> {
                final ConfigurationNode child = node.appendListNode();
                try {
                    entrySerial.serialize(entryType, el, child);
                } catch (final SerializationException ex) {
                    ex.initPath(child::path);
                    throw ex;
                }
            });
        }
    }

    protected void forEachElement(final Collection<?> collection, final CheckedConsumer<Object, SerializationException> action) throws SerializationException {
        for (Object el: collection) {
            action.accept(el);
        }
    }
}