package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.LocationPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityHeadLookPacket.class)
public class SEntityHeadLookPacketMixin implements LocationPacket {
    @Shadow private int entityId;

    @Override
    public int getEntity() {
        return entityId;
    }
}
