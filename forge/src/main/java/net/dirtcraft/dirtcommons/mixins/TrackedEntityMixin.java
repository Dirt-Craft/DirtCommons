package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.vanish.FBIAgentPacket;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.world.TrackedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TrackedEntity.class)
public class TrackedEntityMixin {
    /*
    @Redirect(method = "broadcastAndSend", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/ServerPlayNetHandler;send(Lnet/minecraft/network/IPacket;)V"))
    public void onBroadcast(ServerPlayNetHandler instance, IPacket<?> p_147359_1_) {
        if (p_147359_1_ instanceof FBIAgentPacket) ((FBIAgentPacket) p_147359_1_).addViewerData(instance.player);
    }

     */
}
