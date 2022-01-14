/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.dirtcraft.commons.permission;

import com.mojang.authlib.GameProfile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.context.MutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.IContext;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener to route permission checks made via fabric-permissions-api to LuckPerms
 */
public class LuckPermissionHandler implements IPermissionHandler {
    private final Map<String, DefaultPermissionLevel> defaults = new HashMap<>();
    private final Map<String, String> descriptions = new HashMap<>();
    private LuckPerms plugin;
    private QueryOptions defaultQuery;

    public LuckPermissionHandler() {
    }

    public void initialize(FMLServerStartedEvent event){
        this.plugin = LuckPermsProvider.get();
    }

    @Override
    public void registerNode(@NonNull String node, @NonNull DefaultPermissionLevel level, @NonNull String desc) {
        if (plugin == null) DefaultPermissionHandler.INSTANCE.registerNode(node, level, desc);
        defaults.put(node, level);
        descriptions.put(node, desc);
    }

    @Override
    @NonNull
    public Collection<String> getRegisteredNodes() {
        if (plugin == null) return DefaultPermissionHandler.INSTANCE.getRegisteredNodes();
        return new ArrayList<>(defaults.keySet());
    }

    @Override
    public boolean hasPermission(@NonNull GameProfile profile, @NonNull String node, @Nullable IContext context) {
        if (plugin == null) return DefaultPermissionHandler.INSTANCE.hasPermission(profile, node, context);

        User user = plugin.getUserManager().getUser(profile.getId());
        if (user == null) return defaults.get(node) == DefaultPermissionLevel.ALL;


        final QueryOptions queryOptions;
        if (context != null && context.getPlayer() != null) queryOptions = user.getQueryOptions();
        else if (context != null && context.getWorld() != null) {
            MutableContextSet ctxSet = plugin.getContextManager().getStaticContext().mutableCopy();
            ctxSet.add(DefaultContextKeys.WORLD_KEY, context.getWorld().dimension().getRegistryName().toString());
            queryOptions = QueryOptions.contextual(ctxSet);
        } else queryOptions = getDefaultQuery();


        @NonNull Tristate result = user.getCachedData().getPermissionData(queryOptions).checkPermission(node);

        switch (result) {
            case TRUE: return true;
            case FALSE: return false;
            case UNDEFINED: return defaults.get(node) == DefaultPermissionLevel.ALL;
            default: throw new AssertionError();
        }
    }

    @Override
    @NonNull
    public String getNodeDescription(@NonNull String node) {
        if (plugin == null) return DefaultPermissionHandler.INSTANCE.getNodeDescription(node);
        String desc = descriptions.get(node);
        return desc == null? "": desc;
    }

    public QueryOptions getDefaultQuery(){
        if (defaultQuery == null) defaultQuery = QueryOptions.contextual(plugin.getContextManager().getStaticContext());
        return defaultQuery;
    }
}