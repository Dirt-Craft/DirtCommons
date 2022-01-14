package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.vanish.PrivilegedPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityVelocityPacket.class)
public class SEntityVelocityPacketMixin implements PrivilegedPacket {
    @Shadow private int id;

    @Override
    public int getEntity() {
        return id;
    }
}
