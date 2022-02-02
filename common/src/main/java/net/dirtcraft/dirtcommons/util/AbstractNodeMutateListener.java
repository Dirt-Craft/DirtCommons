package net.dirtcraft.dirtcommons.util;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.dirtcraft.dirtcommons.user.PlayerList;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractNodeMutateListener<T extends CommonsPlayer<?, ?, ?>, U extends AbstractNodeMutateListener<T, U>> {
    private LuckPerms lp;
    private final Consumer<T> onUpdate;
    private final Set<String> permissions;
    private final Set<String> metaNodes;
    private PlayerList<T, ?> pList;
    private List<EventSubscription<?>> subs;
    private boolean prefix;
    private boolean suffix;

    public AbstractNodeMutateListener(Consumer<T> onUpdate) {
        this.onUpdate = onUpdate;
        metaNodes = new HashSet<>();
        permissions = new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public U addPrefix(){
        this.prefix = true;
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public U addSuffix(){
        this.suffix = true;
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public U addTrackedMetaNode(String node) {
        metaNodes.add(node);
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
        permissions.add(node);
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


    private boolean isWatched(Node node) {
        if (NodeType.INHERITANCE.matches(node)) return true;
        else if (NodeType.PREFIX.matches(node)) return prefix;
        else if (NodeType.SUFFIX.matches(node)) return suffix;
        else if (NodeType.META.matches(node)) return metaNodes.contains(NodeType.META.cast(node).getMetaKey());
        else return permissions.contains(node.getKey());
    }


    public void onMutate(Node node, PermissionHolder permissionHolder) {
        if (!isWatched(node)) return;
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

    protected abstract PlayerList<T, ?> getPlayerList();
}
