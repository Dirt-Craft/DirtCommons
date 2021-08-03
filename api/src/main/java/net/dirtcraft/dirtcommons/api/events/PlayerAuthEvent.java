package net.dirtcraft.dirtcommons.api.events;

import java.util.UUID;

public interface PlayerAuthEvent extends Event {
    /**
     * Gets the uuid of the connecting player.
     */
    UUID getPlayerId();

    /**
     * Gets the name of the connecting player.
     */
    String getPlayerName();

    /**
     * Cancels the event and disconnects the user with the specified reason
     * @param reason the reason, either a string or the platforms text equivalent.
     */
    void cancel(Object reason);

    /**
     * Cancels the event and disconnects the user with the specified reason
     * @param reason the reason.
     */
    void cancel(String reason);
}
