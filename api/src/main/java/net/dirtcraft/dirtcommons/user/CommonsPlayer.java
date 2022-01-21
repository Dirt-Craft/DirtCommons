package net.dirtcraft.dirtcommons.user;

import net.dirtcraft.dirtcommons.permission.Permissible;
import net.luckperms.api.model.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;
import java.util.function.Function;

public interface CommonsPlayer<T extends S, S> extends Permissible, Vanishable<S>, TeamPlayer {

    T getServerEntity();

    User getUser();

    String getUserName();

    UUID getUserId();

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

    default String getUserSuffix(User user){
        return getUser().getCachedData()
                .getMetaData()
                .getSuffix();
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
