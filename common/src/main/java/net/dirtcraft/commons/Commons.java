package net.dirtcraft.commons;

import net.dirtcraft.commons.util.Cycler;
import net.dirtcraft.commons.configuration.serializers.CyclerSerializer;
import net.dirtcraft.commons.configuration.serializers.HashSetSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Commons {
    private BiConsumer<Type, TypeSerializer<?>> register;
    public Commons() {
        try {
            final TypeSerializerCollection serializers = TypeSerializerCollection.defaults();
            Field field = serializers.getClass().getDeclaredField("serializers");
            field.setAccessible(true);
            field.set(serializers, new ArrayList<>((List<?>) field.get(serializers)));
            List<?> serializerList = (List<?>) field.get(serializers);
            Constructor<?> inner = Stream.of(TypeSerializerCollection.class.getDeclaredClasses())
                    .filter(c->"RegisteredSerializer".equals(c.getSimpleName()))
                    .findFirst()
                    .map(clazz->clazz.getDeclaredConstructors()[0])
                    .orElse(null);
            inner.setAccessible(true);
            register = (type, serializer) -> {
                try {
                    ((List<Object>)serializerList).add((inner.newInstance((Predicate<Type>)(test -> test.equals(type)), serializer)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        register.accept(HashSet.class, new HashSetSerializer());
        register.accept(Cycler.class, new CyclerSerializer());
    }

    public void registerDefaultSerializer(Type type, TypeSerializer<?> serializer) {
        register.accept(type, serializer);
    }
}
