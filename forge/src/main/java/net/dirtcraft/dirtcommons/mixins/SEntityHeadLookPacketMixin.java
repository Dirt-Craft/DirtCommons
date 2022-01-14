package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.vanish.PrivilegedPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityHeadLookPacket.class)
public class SEntityHeadLookPacketMixin implements PrivilegedPacket {
    @Shadow private int entityId;

    @Override
    public int getEntity() {
        return entityId;
    }
}
