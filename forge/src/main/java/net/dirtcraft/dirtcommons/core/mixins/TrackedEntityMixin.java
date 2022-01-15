package net.dirtcraft.dirtcommons.core.mixins;

import net.minecraft.world.TrackedEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TrackedEntity.class)
public class TrackedEntityMixin {
    /*
    @Redirect(method = "broadcastAndSend", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/ServerPlayNetHandler;send(Lnet/minecraft/network/IPacket;)V"))
    public void onBroadcast(ServerPlayNetHandler instance, IPacket<?> p_147359_1_) {
        if (p_147359_1_ instanceof FBIAgentPacket) ((FBIAgentPacket) p_147359_1_).addViewerData(instance.player);
    }

     */
}
