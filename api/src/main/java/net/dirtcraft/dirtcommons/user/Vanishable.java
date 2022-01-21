package net.dirtcraft.dirtcommons.user;

import java.util.Collection;

public interface Vanishable<T> extends VanishSubject {
    short getVanishLevel();

    void setVanishLevel(short v);

    boolean isTracking(T entity);

    void addTrackedEntities(Collection<? extends T> entities);

    void removeTrackedEntities(Collection<? extends T> entities);

    void clearTrackedEntities();

    boolean canSeePlayerOutlines();

    void setSeePlayerOutlines(boolean value);
}
