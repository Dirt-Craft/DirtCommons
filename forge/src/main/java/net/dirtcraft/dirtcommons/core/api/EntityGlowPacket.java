package net.dirtcraft.dirtcommons.core.api;

import net.minecraft.network.datasync.EntityDataManager;

public interface EntityGlowPacket extends TrackedViewerPacket {
    void forceGlow(EntityDataManager.DataEntry<Byte> data, boolean value);
}
