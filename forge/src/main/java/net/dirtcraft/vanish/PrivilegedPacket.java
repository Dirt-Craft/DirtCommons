package net.dirtcraft.vanish;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.ServerPlayNetHandler;

public interface PrivilegedPacket {
    int getEntity();
}
