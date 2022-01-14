package net.dirtcraft.vanish;

import net.minecraft.network.datasync.EntityDataManager;

public interface FBIAgentPacket extends TrackedViewerPacket {
    void forceGlow(EntityDataManager.DataEntry<Byte> data, boolean value);
}
