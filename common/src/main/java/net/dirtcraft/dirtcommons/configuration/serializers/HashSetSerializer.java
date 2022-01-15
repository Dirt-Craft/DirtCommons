package net.dirtcraft.dirtcommons.configuration.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.AbstractListChildSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.CheckedConsumer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

@SuppressWarnings({"unchecked", "rawtypes"})
public class HashSetSerializer extends AbstractListChildSerializer<HashSet<?>> {
    @Override
    protected Type elementType(final Type containerType) throws SerializationException {
        if (!(containerType instanceof ParameterizedType)) {
            throw new SerializationException("Raw types are not supported for collections");
        }
        return ((ParameterizedType) containerType).getActualTypeArguments()[0];
    }

    @Override
    protected HashSet<?> createNew(final int length, final Type elementType) {
        return new HashSet<>(length);
    }

    @Override
    protected void forEachElement(final HashSet<?> collection, final CheckedConsumer<Object, SerializationException> action) throws SerializationException {
        for (Object el: collection) {
            action.accept(el);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void deserializeSingle(final int index, final HashSet<?> collection, final @Nullable Object deserialized) {
        ((HashSet) collection).add(deserialized);
    }

}