package net.dirtcraft.dirtcommons.user;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.UUID;

public interface PlayerList<T extends CommonsPlayer<?, ?>> {
    List<T> getOnlinePlayers();
    @Nullable T getOnlinePlayer(UUID uuid);

    void update(T player);
    void addPseudoTeam(T player);
    void removePseudoTeam(T player);
}
