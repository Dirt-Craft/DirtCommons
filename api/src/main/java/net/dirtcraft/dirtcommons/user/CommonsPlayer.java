package net.dirtcraft.dirtcommons.user;

import net.dirtcraft.dirtcommons.DirtCommons;
import net.dirtcraft.dirtcommons.permission.CommandSource;
import net.dirtcraft.dirtcommons.permission.Permissions;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public interface CommonsPlayer<T extends S, S, U> extends CommandSource<U>, Vanishable<S>, TeamPlayer {

    T getServerEntity();

    User getUser();

    String getUserName();

    UUID getUserId();


    void sendTitleTimes(int fadeIn, int stay, int fadeOut);
    void sendTitle(U title);
    void sendSubTitle(U title);
    void clearTitle();

    default void sendTitle(U title, int fadeIn, int stay, int fadeOut) {
        sendTitleTimes(fadeIn, stay, fadeOut);
        sendTitle(title);
    }

    default void sendSubTitle(U title, int fadeIn, int stay, int fadeOut) {
        sendTitleTimes(fadeIn, stay, fadeOut);
        sendSubTitle(title);
    }


    default void setTitle(U title, U subtitle, int fadeIn, int stay, int fadeOut) {
        clearTitle();
        sendTitleTimes(fadeIn, stay, fadeOut);
        if (title != null) sendTitle(title);
        if (subtitle != null) sendSubTitle(subtitle);
    }

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

    default String getNickname() {
        return getMeta(Permissions.NICKNAME);
    }

    default String getUserRankIndicator(){
        return getMeta(Permissions.RANK_INDICATOR);
    }

    //todo cache this, use node watcher to update.
    default boolean rankIndicatorRedundant(){
        User user = getUser();
        Collection<Group> groups = user.getInheritedGroups(user.getQueryOptions());
        String prefix = getUserPrefix();
        String indicator = getUserRankIndicator();
        Permissions helper = DirtCommons.getInstance().getPermissionHelper();
        if (indicator == null || prefix == null) return true;
        for (Group group : groups) {
            if (!prefix.equals(group.getCachedData().getMetaData().getPrefix())) continue;
            if (indicator.equals(helper.getGroupIndicator(group.getName()))) return true;
        }
        return false;
    }

    default <V> V getMetaOrDefault(String key, Function<String, V> mapper, V def){
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
