package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.vanish.PrivilegedPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityTeleportPacket.class)
public class SEntityTeleportPacketMixin implements PrivilegedPacket {
    @Shadow private int id;

    @Override
    public int getEntity() {
        return id;
    }
}
