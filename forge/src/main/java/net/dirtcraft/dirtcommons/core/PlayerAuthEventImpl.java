package net.dirtcraft.dirtcommons.core;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.api.events.PlayerAuthEvent;

import java.net.SocketAddress;
import java.util.UUID;

public class PlayerAuthEventImpl implements PlayerAuthEvent {
    public final UUID uuid;
    public final String name;
    public final SocketAddress address;
    public volatile boolean canceled;
    public volatile Object reason;
    private volatile boolean complete = false;

    public PlayerAuthEventImpl(GameProfile profile, SocketAddress address) {
        this.uuid = profile.getId();
        this.name = profile.getName();
        this.address = address;
    }

    @Override
    public UUID getPlayerId() {
        return uuid;
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    @Override
    public void cancel(Object reason) {
        this.reason = reason;
    }

    @Override
    public void cancel(String reason) {
        this.reason = reason;
    }

    @Override
    public void setCancelled() {
        this.canceled = true;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    public boolean isComplete(){
        return complete;
    }


}
