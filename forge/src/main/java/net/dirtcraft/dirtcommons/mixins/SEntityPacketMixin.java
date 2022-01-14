package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.vanish.PrivilegedPacket;
import net.minecraft.network.play.server.SEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityPacket.class)
public class SEntityPacketMixin implements PrivilegedPacket {
    @Shadow protected int entityId;

    @Override
    public int getEntity() {
        return entityId;
    }
}
