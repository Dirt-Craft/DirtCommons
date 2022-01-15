package net.dirtcraft.dirtcommons.core.api;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface TrackedViewerPacket {
    void addViewerData(ServerPlayerEntity entity);
}
