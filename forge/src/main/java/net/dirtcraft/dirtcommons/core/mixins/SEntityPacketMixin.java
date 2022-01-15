package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.LocationPacket;
import net.minecraft.network.play.server.SEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SEntityPacket.class)
public class SEntityPacketMixin implements LocationPacket {
    @Shadow protected int entityId;

    @Override
    public int getEntity() {
        return entityId;
    }
}
