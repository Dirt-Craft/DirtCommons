package net.dirtcraft.dirtcommons.core.mixins;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.dirtcraft.dirtcommons.core.api.LocationPacket;
import net.dirtcraft.dirtcommons.core.api.TrackedViewerPacket;
import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.ServerPlayNetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetHandler.class)
public abstract class ServerPlayNetHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "send(Lnet/minecraft/network/IPacket;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacketA(IPacket<?> p_211148_1_, GenericFutureListener<? extends Future<? super Void>> p_211148_2_, CallbackInfo ci){
        screenPackets(p_211148_1_, ci);
    }

    @Unique
    private void screenPackets(IPacket<?> iPacket, CallbackInfo ci) {
        if (iPacket instanceof TrackedViewerPacket) ((TrackedViewerPacket) iPacket).addViewerData(player);
        if (iPacket instanceof LocationPacket) {
            LocationPacket privileged = (LocationPacket) iPacket;
            Entity e = player.level.getEntity(privileged.getEntity());
            if (e instanceof CommonsPlayer && !canSeePlayer((CommonsPlayer) player, (CommonsPlayer) e)) ci.cancel();
        }
    }


    public boolean canSeePlayer(CommonsPlayer viewer, CommonsPlayer target) {
        return viewer.getVanishViewLevel() >= target.getVanishLevel();
    }
}
