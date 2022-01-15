package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.LocationPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityVelocityPacket.class)
public class SEntityVelocityPacketMixin implements LocationPacket {
    @Shadow private int id;

    @Override
    public int getEntity() {
        return id;
    }
}
