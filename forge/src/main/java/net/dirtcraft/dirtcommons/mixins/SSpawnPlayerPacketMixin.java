package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.vanish.PrivilegedPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SSpawnPlayerPacket.class)
public class SSpawnPlayerPacketMixin implements PrivilegedPacket {

    @Shadow private int entityId;

    @Override
    public int getEntity() {
        return entityId;
    }
}
