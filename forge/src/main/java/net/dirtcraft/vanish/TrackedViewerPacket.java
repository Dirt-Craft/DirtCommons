package net.dirtcraft.vanish;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface TrackedViewerPacket {
    void addViewerData(ServerPlayerEntity entity);
}
