package net.dirtcraft.dirtcommons.util;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.dirtcraft.dirtcommons.user.PlayerList;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractNodeMutateListener<T extends CommonsPlayer<?, ?>, U extends AbstractNodeMutateListener<T, U>> {
    private LuckPerms lp;
    private Consumer<T> onUpdate;
    private PlayerList<T> pList;
    private List<String> nodes;

    public AbstractNodeMutateListener(Consumer<T> onUpdate) {
        this.onUpdate = onUpdate;
        nodes = new ArrayList<>();
        nodes.add("group");
    }

    @SuppressWarnings("unchecked")
    public U addTrackedMetaNode(String node) {
        nodes.add("meta." + (node.replaceAll("\\.", "\\\\.")));
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public U addTrackedMetaNodes(Collection<String> nodes) {
        for (String node: nodes) addTrackedMetaNode(node);
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public U addTrackedMetaNodes(String... nodes) {
        for (String node: nodes) addTrackedMetaNode(node);
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public U addTrackedNode(String node) {
        nodes.add(node);
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public U addTrackedNodes(Collection<String> nodes) {
        for (String node: nodes) addTrackedNode(node);
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public U addTrackedNodes(String... nodes) {
        for (String node: nodes) addTrackedNode(node);
        return (U) this;
    }

    private List<EventSubscription<?>> subs;

    public void onMutate(Node node, PermissionHolder permissionHolder) {
        if (!matches(node.getKey())) return;
        if (permissionHolder.getIdentifier().getType().equals(PermissionHolder.Identifier.GROUP_TYPE)) {
            Group group = lp.getGroupManager().getGroup(permissionHolder.getIdentifier().getName());
            if (group == null) {
                System.out.printf("Tried to update a watched node {%s} on a group {%s} update, but the group was null", node.getKey(), permissionHolder.getFriendlyName());
            } else for (T player : getUsersWithGroup(group)) onUpdate.accept(player);
        } else {
            onUpdate.accept(pList.getOnlinePlayer(((User) permissionHolder).getUniqueId()));
        }
    }

    public void onNodeAdd(NodeAddEvent event) {
        onMutate(event.getNode(), event.getTarget());
    }

    public void onNodeRemove(NodeRemoveEvent event) {
        onMutate(event.getNode(), event.getTarget());
    }

    public void onNodeClear(NodeClearEvent event) {
        for (Node node : event.getNodes()) onMutate(node, event.getTarget());
    }

    public void close(){
        subs.forEach(EventSubscription::close);
    }

    protected void init(LuckPerms lp) {
        this.lp = lp;
        subs = Arrays.asList(
                lp.getEventBus().subscribe(NodeAddEvent.class, this::onNodeAdd),
                lp.getEventBus().subscribe(NodeClearEvent.class, this::onNodeClear),
                lp.getEventBus().subscribe(NodeRemoveEvent.class, this::onNodeRemove)
        );
        this.pList = getPlayerList();
    }

    private List<T> getUsersWithGroup(Group group) {
        String groupNode = String.format("group.%s", group.getName());
        List<T> players = new ArrayList<>();
        lp.getUserManager().getLoadedUsers().forEach(user->{
            T player = pList.getOnlinePlayer(user.getUniqueId());
            if (player == null || !player.hasPermission(groupNode)) return;
            players.add(player);
        });
        return players;
    }

    private boolean matches(String node) {
        for (String key : nodes) if (node.startsWith(key)) return true;
        return false;
    }

    protected abstract PlayerList<T> getPlayerList();
}
