package net.dirtcraft.dirtcommons.permission;

import java.util.UUID;

public interface Permissions {
    boolean initialized();
    boolean hasPermission(UUID user, String node);
    void setUserPermission(String user, String node, boolean value);
    void setUserMeta(String user, String node, String value);
    void setGroupPermission(String group, String node, boolean value);
    void setGroupMeta(String group, String node, String value);
}
