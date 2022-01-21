package net.dirtcraft.dirtcommons.permission;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Permissible {
    boolean hasPermission(@NonNull String node);
}
