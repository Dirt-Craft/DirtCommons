package net.dirtcraft.dirtcommons.permission;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public interface Permissions {
    String NICKNAME = "commons.nickname"; //meta
    String RANK_INDICATOR = "commons.indicator"; //meta
    String COLORS_USE = "commons.formatting.colors"; //node
    String COLORS_FORMAT = "commons.formatting.style"; //node
    String COLORS_HEX = "commons.formatting.advanced"; //node
    String COLORS_STAFF = "commons.formatting.staff"; //node

    boolean initialized();

    String getServerContext();

    boolean hasPermission(UUID user, String node);

    void setUserPermission(UUID user, String node, boolean value);
    void clearUserMeta(UUID user, String node);
    void setUserMeta(UUID user, String node, String value);
    void setUserPrefix(UUID user, int minPriority, String value);
    void setUserPrefix(UUID user, String value);
    void setUserSuffix(UUID user, int minPriority, String value);
    void setUserSuffix(UUID user, String value);

    String getUserMeta(UUID user, String node);
    String getUserPrefix(UUID user);
    String getUserSuffix(UUID user);
    String getUserGroup(UUID user);
    Collection<String> getUserGroups(UUID user);
    <T> T getUserMetaOrDefault(UUID uuid, String key, Function<String, T> mapper, T def);

    String getGroupMeta(String group, String node);
    String getGroupPrefix(String group);
    String getGroupSuffix(String group);

    void setGroupPermission(String group, String node, boolean value);
    void clearGroupMeta(String group, String node);
    void setGroupMeta(String group, String node, String value);
    int getGroupWeight(String group);
    void setGroupPrefix(String group, String value);
    void setGroupSuffix(String group, String value);

    String getUserNick(UUID uuid);

    void setUserNick(UUID uuid, String nick);


    default String getUserIndicator(UUID user) {
        return getUserMeta(user, RANK_INDICATOR);
    }


    default String getGroupIndicator(String group) {
        return getGroupMeta(group, RANK_INDICATOR);
    }
}
