package net.dirtcraft.dirtcommons.util;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.ForgeCommons;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.permission.Permissions;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
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
        if (lp.getGroupManager().getGroup(group) == null) return;
        lp.getGroupManager().modifyGroup(group, g->{
            g.data().clear(contexts, n->isMatchingMeta(n, node));
        });
    }

    @Override
    public void setGroupMeta(String group, String node, String value){
        if (lp.getGroupManager().getGroup(group) == null) return;
        if (value == null) clearGroupMeta(group, node);
        else lp.getGroupManager().modifyGroup(group, g->{
            g.data().clear(contexts, n->isMatchingMeta(n, node));
            g.data().add(MetaNode.builder(node, value).context(contexts).build());
        });
    }

    @Override
    public void setGroupPrefix(String group, String value){
        if (lp.getGroupManager().getGroup(group) == null) return;
        if (value == null) lp.getGroupManager().modifyGroup(group, g-> g.data().clear(contexts, NodeType.PREFIX::matches));
        else lp.getGroupManager().modifyGroup(group, g->{
            g.data().clear(contexts, NodeType.PREFIX::matches);
            Map<Integer, String> inheritedPrefixes = g.getCachedData().getMetaData(QueryOptions.contextual(contexts)).getPrefixes();
            int priority = inheritedPrefixes.keySet().stream().mapToInt(i -> i + 10).max().orElse(10);
            g.data().add(PrefixNode.builder(value, priority).context(contexts).build());
        });
    }

    @Override
    public void setGroupSuffix(String group, String value){
        if (lp.getGroupManager().getGroup(group) == null) return;
        if (value == null) lp.getGroupManager().modifyGroup(group, g-> g.data().clear(contexts, NodeType.SUFFIX::matches));
        else lp.getGroupManager().modifyGroup(group, g->{
            g.data().clear(contexts, NodeType.SUFFIX::matches);
            Map<Integer, String> inheritedSuffixes = g.getCachedData().getMetaData(QueryOptions.contextual(contexts)).getSuffixes();
            int priority = inheritedSuffixes.keySet().stream().mapToInt(i -> i + 10).max().orElse(10);
            g.data().add(SuffixNode.builder(value, priority).context(contexts).build());
        });
    }

    @Override
    public void setGroupPermission(String group, String node, boolean value){
        String command = String.format("lp group %s permission set %s %b %s", group, node, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public void clearUserMeta(UUID user, String node) {
        lp.getUserManager().modifyUser(user, u-> u.data().clear(contexts, n->isMatchingMeta(n, node)));
    }

    @Override
    public void setUserMeta(UUID user, String node, String value){
        if (value == null) clearUserMeta(user, node);
        else lp.getUserManager().modifyUser(user, u->{
            u.data().clear(contexts, n->isMatchingMeta(n, node));
            u.data().add(MetaNode.builder(node, value).context(contexts).build());
        });
    }

    @Override
    public void setUserNick(UUID user, String value){
        setUserMeta(user, Permissions.NICKNAME, value);
    }

    @Override
    public void setUserPrefix(UUID user, String value){
        this.setUserPrefix(user, 0, value);
    }

    @Override
    public void setUserSuffix(UUID user, String value){
        this.setUserSuffix(user, 0, value);
    }

    @Override
    public void setUserPrefix(UUID user, int minPriority, String value){
        if (value == null) lp.getUserManager().modifyUser(user, u-> u.data().clear(contexts, NodeType.PREFIX::matches));
        else lp.getUserManager().modifyUser(user, u->{
            u.data().clear(contexts, NodeType.PREFIX::matches);
            Map<Integer, String> inheritedPrefixes = u.getCachedData().getMetaData(QueryOptions.contextual(contexts)).getPrefixes();
            int priority = Math.max(inheritedPrefixes.keySet().stream().mapToInt(i -> i + 10).max().orElse(10), minPriority);
            u.data().add(PrefixNode.builder(value, priority).context(contexts).build());
        });
    }

    @Override
    public void setUserSuffix(UUID user, int minPriority, String value){
        if (value == null) lp.getUserManager().modifyUser(user, u-> u.data().clear(contexts, NodeType.SUFFIX::matches));
        else lp.getUserManager().modifyUser(user, u->{
            u.data().clear(contexts, NodeType.SUFFIX::matches);
            Map<Integer, String> inheritedSuffixes = u.getCachedData().getMetaData(QueryOptions.contextual(contexts)).getSuffixes();
            int priority = Math.max(inheritedSuffixes.keySet().stream().mapToInt(i -> i + 10).max().orElse(10), minPriority);
            u.data().add(SuffixNode.builder(value, priority).context(contexts).build());
        });
    }

    @Override
    public <T> T getUserMetaOrDefault(UUID uuid, String key, Function<String, T> mapper, T def){
        try {
            User user = lp.getUserManager().getUser(uuid);
            if (user == null) return def;
            String val = getUserMeta(user, key);
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
    public String getUserNick(UUID uuid){
        return getUserMeta(uuid, Permissions.NICKNAME);
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
    public int getGroupWeight(String group) {
        Group g = lp.getGroupManager().getGroup(group);
        if (g == null) return -1;
        else return g.getWeight().orElse(0);
    }

    @Override
    public void setUserPermission(UUID user, String node, boolean value){
        String command = String.format("lp user %s permission set %s %b %s", getUserInput(user), node, value, getServerContext());
        commands.performCommand(console, command);
    }

    @Override
    public boolean hasPermission(UUID user, String node) {
        return hasPermission(new GameProfile(user, ""), node, null);
    }

    public <T> T getUserMetaOrDefault(User user, String key, Function<String, T> mapper, T def){
        try {
            String val = getUserMeta(user, key);
            if (val == null) return def;
            else return mapper.apply(val);
        } catch (Exception e){
            return def;
        }
    }

    public String getUserMeta(User user, String key){
        if (user == null) return null;
        else return user.getCachedData()
                .getMetaData()
                .getMetaValue(key);
    }

    public String getUserSuffix(User user) {
        if (user == null) return null;
        else return user.getCachedData()
                .getMetaData()
                .getSuffix();
    }

    public String getUserPrefix(User user){
        if (user == null) return null;
        else return user.getCachedData()
                .getMetaData()
                .getPrefix();
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
        this.console = event.getServer().createCommandSourceStack()
                .withPermission(4);
        this.commands = event.getServer().getCommands();
    }

    private String getUserInput(UUID playerId){
        ForgePlayer p = ForgeCommons.getInstance().getPlayers().getOnlinePlayer(playerId);
        return p == null? playerId.toString() : p.getUserName();
    }

    private boolean isMatchingMeta(Node node, String key) {
        if (!NodeType.META.matches(node)) return false;
        else return key.equals(NodeType.META.cast(node).getMetaKey());
    }
}
