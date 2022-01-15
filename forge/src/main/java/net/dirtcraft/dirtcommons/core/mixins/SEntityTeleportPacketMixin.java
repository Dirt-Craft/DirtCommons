package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.LocationPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityTeleportPacket.class)
public class SEntityTeleportPacketMixin implements LocationPacket {
    @Shadow private int id;

    @Override
    public int getEntity() {
        return id;
    }
}
