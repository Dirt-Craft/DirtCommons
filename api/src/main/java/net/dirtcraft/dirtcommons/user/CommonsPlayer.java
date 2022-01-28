package net.dirtcraft.dirtcommons.user;

import net.dirtcraft.dirtcommons.DirtCommons;
import net.dirtcraft.dirtcommons.permission.Permissible;
import net.dirtcraft.dirtcommons.permission.Permissions;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public interface CommonsPlayer<T extends S, S> extends Permissible, Vanishable<S>, TeamPlayer {

    T getServerEntity();

    User getUser();

    String getUserName();

    UUID getUserId();

    String getNickname();

    void setNickname(String nick);

    default String getMeta(String key){
        return getUser().getCachedData()
                .getMetaData()
                .getMetaValue(key);
    }

    default String getUserPrefix(){
        return getUser().getCachedData()
                .getMetaData()
                .getPrefix();
    }

    default String getUserSuffix(){
        return getUser().getCachedData()
                .getMetaData()
                .getSuffix();
    }

    default String getUserGroup(){
        return getUser().getPrimaryGroup();
    }

    default String getUserRankIndicator(){
        return getUser().getCachedData()
                .getMetaData()
                .getMetaValue(Permissions.RANK_INDICATOR);
    }

    default boolean rankIndicatorRedundant(){
        User user = getUser();
        Collection<Group> groups = user.getInheritedGroups(user.getQueryOptions());
        String prefix = getUserPrefix();
        String indicator = getUserRankIndicator();
        Permissions helper = DirtCommons.getInstance().getPermissionHelper();
        if (prefix == null) return true;
        for (Group group : groups) {
            if (!prefix.equals(group.getCachedData().getMetaData().getPrefix())) continue;
            if (indicator.equals(helper.getGroupIndicator(group.getName()))) return true;
        }
        return false;
    }

    default <U> U getMetaOrDefault(String key, Function<String, U> mapper, U def){
        try {
            String val = getMeta(key);
            if (val == null) return def;
            else return mapper.apply(val);
        } catch (Exception e){
            return def;
        }
    }

    default boolean hasPermission(@NonNull String node) {
        return getUser()
                .getCachedData()
                .getPermissionData()
                .checkPermission(node)
                .asBoolean();
    }
}
