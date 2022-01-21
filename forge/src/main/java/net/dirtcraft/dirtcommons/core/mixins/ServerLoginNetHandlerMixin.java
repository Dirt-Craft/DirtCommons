package net.dirtcraft.dirtcommons.core.mixins;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.event.AuthenticationEvent;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetHandler.class)
public abstract class ServerLoginNetHandlerMixin {
    @Shadow @Final private MinecraftServer server;

    @Shadow private GameProfile gameProfile;

    @Shadow public abstract void disconnect(ITextComponent p_194026_1_);

    @Unique private boolean sentNegotiationEvent;
    @Unique private AuthenticationEvent authenticationEvent;


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/network/NetworkHooks;tickNegotiation(Lnet/minecraft/network/login/ServerLoginNetHandler;Lnet/minecraft/network/NetworkManager;Lnet/minecraft/entity/player/ServerPlayerEntity;)Z"))
    public void onNegotiation(CallbackInfo ci){
        if (sentNegotiationEvent) return;
        sentNegotiationEvent = true;
        authenticationEvent = new AuthenticationEvent(server, gameProfile);
        if (MinecraftForge.EVENT_BUS.post(authenticationEvent)) {
            ITextComponent message = authenticationEvent.getCancelReason();
            disconnect(message);
        }
    }

    @Inject(method = "tick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/login/ServerLoginNetHandler;handleAcceptedLogin()V"))
    public void onHello(CallbackInfo ci) {
        if (!authenticationEvent.isDone()) ci.cancel();
    }
}
