package net.dirtcraft.dirtcommons.permission;

import java.util.Collection;
import java.util.UUID;

public interface Permissions {
    String RANK_INDICATOR = "rank.indicator";
    boolean initialized();
    String getServerContext();
    boolean hasPermission(UUID user, String node);

    void setUserPermission(UUID user, String node, boolean value);
    void clearUserMeta(UUID user, String node);
    void setUserMeta(UUID user, String node, String value);
    void setUserPrefix(UUID user, String value);
    void setUserSuffix(UUID user, String value);

    Collection<String> getUserGroups(UUID user);
    String getUserGroup(UUID user);
    String getUserIndicator(UUID user);
    String getGroupMeta(String group, String node);
    String getGroupPrefix(String group);
    String getGroupSuffix(String group);
    String getGroupIndicator(String group);

    void setGroupPermission(String group, String node, boolean value);
    void clearGroupMeta(String group, String node);
    void setGroupMeta(String group, String node, String value);
    void setGroupPrefix(String group, String value);
    void setGroupSuffix(String group, String value);
}
