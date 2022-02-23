package net.dirtcraft.dirtcommons.event;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;

public class DisconnectEvent extends Event {
    public final MinecraftServer server;
    public final GameProfile profile;

    public DisconnectEvent(MinecraftServer server, GameProfile profile) {
        this.server = server;
        this.profile = profile;

    }
}
