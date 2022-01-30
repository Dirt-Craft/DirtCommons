package net.dirtcraft.dirtcommons.util;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.permission.Permissions;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PermissionHelper implements Permissions {
    private Commands commands;
    private CommandSource console;
    private ImmutableContextSet contexts;
    private LuckPerms lp;

    public PermissionHelper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getServerContext(){
        return contexts.getAnyValue("server").orElse("global");
    }

    @Override
    public boolean initialized() {
        return lp != null;
    }

    @Override
    public void clearGroupMeta(String group, String node) {
        String command = String.format("lp group %s meta clear %s %s", group, node, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void setGroupMeta(String group, String node, String value){
        if (value == null) {
            clearGroupMeta(group, node);
            return;
        }
        value = value.replaceAll("\"", "");
        String command = String.format("lp group %s meta set %s \"%s\" %s", group, node, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void setGroupPrefix(String group, String value){
        if (value == null) {
            clearGroupMeta(group, "prefix");
            return;
        }
        value = value.replaceAll("\"", "");
        String command = String.format("lp group %s meta setprefix \"%s\" %s", group, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void setGroupSuffix(String group, String value){
        if (value == null) {
            clearGroupMeta(group, "suffix");
            return;
        }
        value = value.replaceAll("\"", "");
        String command = String.format("lp group %s meta setsuffix \"%s\" %s", group, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void setGroupPermission(String group, String node, boolean value){
        String command = String.format("lp group %s permission set %s %b %s", group, node, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void clearUserMeta(UUID user, String node) {
        String command = String.format("lp user %s meta clear %s %s", user.toString(), node, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void setUserMeta(UUID user, String node, String value){
        if (value == null) {
            clearUserMeta(user, node);
            return;
        }
        value = value.replaceAll("\"", "");
        String command = String.format("lp user %s meta set %s \"%s\" %s", user.toString(), node, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void setUserPrefix(UUID user, String value){
        if (value == null) {
            clearUserMeta(user, "prefix");
            return;
        }
        value = value.replaceAll("\"", "");
        String command = String.format("lp user %s meta setprefix \"%s\" %s", user.toString(), value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void setUserSuffix(UUID user, String value){
        if (value == null) {
            clearUserMeta(user, "suffix");
            return;
        }
        value = value.replaceAll("\"", "");
        String command = String.format("lp user %s meta setsuffix \"%s\" %s", user.toString(), value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public <T> T getMetaOrDefault(UUID uuid, String key, Function<String, T> mapper, T def){
        try {
            User user = lp.getUserManager().getUser(uuid);
            if (user == null) return def;
            String val = getMeta(user, key);
            if (val == null) return def;
            else return mapper.apply(val);
        } catch (Exception e){
            return def;
        }
    }

    @Override
    public String getUserMeta(UUID user, String node) {
        User p = lp.getUserManager().getUser(user);
        if (p == null) return null;
        return p.getCachedData()
                .getMetaData()
                .getMetaValue(node);
    }

    @Override
    public String getUserPrefix(UUID uuid){
        User user = lp.getUserManager().getUser(uuid);
        return getUserPrefix(user);
    }

    @Override
    public String getUserSuffix(UUID uuid){
        User user = lp.getUserManager().getUser(uuid);
        return getUserSuffix(user);
    }

    @Override
    public Collection<String> getUserGroups(UUID user) {
        User p = lp.getUserManager().getUser(user);
        if (p == null) return Collections.EMPTY_LIST;
        else return p.getInheritedGroups(p.getQueryOptions())
                .stream()
                .map(Group::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String getUserGroup(UUID user) {
        User p = lp.getUserManager().getUser(user);
        if (p == null) return null;
        else return p.getPrimaryGroup();
    }

    @Override
    public String getGroupMeta(String group, String node) {
        Group g = lp.getGroupManager().getGroup(group);
        if (g == null) return null;
        else return g.getCachedData().getMetaData().getMetaValue(node);
    }

    @Override
    public String getGroupPrefix(String group) {
        Group g = lp.getGroupManager().getGroup(group);
        if (g == null) return null;
        else return g.getCachedData().getMetaData().getPrefix();
    }

    @Override
    public String getGroupSuffix(String group) {
        Group g = lp.getGroupManager().getGroup(group);
        if (g == null) return null;
        else return g.getCachedData().getMetaData().getSuffix();
    }

    @Override
    public void setUserPermission(UUID user, String node, boolean value){
        String command = String.format("lp user %s permission set %s %b %s", user.toString(), node, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public boolean hasPermission(UUID user, String node) {
        return hasPermission(new GameProfile(user, ""), node, null);
    }

    public String getMeta(User user, String key){
        if (user == null) return null;
        else return user.getCachedData()
                .getMetaData()
                .getMetaValue(key);
    }

    public <T> T getMetaOrDefault(User user, String key, Function<String, T> mapper, T def){
        try {
            String val = getMeta(user, key);
            if (val == null) return def;
            else return mapper.apply(val);
        } catch (Exception e){
            return def;
        }
    }

    public String getUserPrefix(User user){
        if (user == null) return null;
        else return user.getCachedData()
                .getMetaData()
                .getPrefix();
    }

    public String getUserSuffix(User user){
        if (user == null) return null;
        else return user.getCachedData()
                .getMetaData()
                .getSuffix();
    }

    public boolean hasPermission(@NonNull GameProfile profile, @NonNull String node, @Nullable IContext context) {
        return PermissionAPI.hasPermission(profile, node, context);
    }

    public boolean hasPermission(@NonNull ServerPlayerEntity player, @NonNull String node) {
        return PermissionAPI.hasPermission(player, node);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerStarting(FMLServerStartingEvent event) {
        this.lp = LuckPermsProvider.get();
        this.contexts = lp.getContextManager().getStaticContext();
        this.console = event.getServer().createCommandSourceStack();
        this.commands = event.getServer().getCommands();
    }
}
