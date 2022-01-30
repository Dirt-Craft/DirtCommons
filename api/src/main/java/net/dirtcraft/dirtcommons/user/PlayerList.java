package net.dirtcraft.dirtcommons.user;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public interface PlayerList<T extends CommonsPlayer<?, ?>> {
    List<T> getOnlinePlayers();
    @Nullable T getOnlinePlayer(UUID uuid);
    @Nullable T getOnlinePlayer(String name);

    void update(T player);
    void addPseudoTeam(T player);
    void removePseudoTeam(T player);

    default List<T> getMatching(Predicate<T> predicate) {
        List<T> list = new ArrayList<>();
        for (T t : getOnlinePlayers()) if (predicate.test(t)) list.add(t);
        return list;
    }
}
