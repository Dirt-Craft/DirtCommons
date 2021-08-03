package net.dirtcraft.dirtcommons.core.mixins;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.core.PlayerAuthEventImpl;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerLoginNetHandler.class)
public abstract class ServerLoginNetHandlerMixin {
    @Shadow @Final private MinecraftServer server;

    @Shadow @Final public NetworkManager connection;

    @Shadow private GameProfile gameProfile;

    @Shadow public abstract void disconnect(ITextComponent p_194026_1_);

    @Unique private final ArrayList<CompletableFuture<?>> luckperms$waitConditions = new ArrayList<>();
    @Unique private boolean luckperms$sentNegotiationEvent = false;
    @Unique private CompletableFuture<?> luckperms$waitFor = null;


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/network/NetworkHooks;tickNegotiation(Lnet/minecraft/network/login/ServerLoginNetHandler;Lnet/minecraft/network/NetworkManager;Lnet/minecraft/entity/player/ServerPlayerEntity;)Z"))
    public void onNegotiation(CallbackInfo ci){
        if (luckperms$sentNegotiationEvent) return;
        luckperms$sentNegotiationEvent = true;
        PlayerAuthEventImpl event = new PlayerAuthEventImpl(gameProfile, connection.getRemoteAddress());
        //todo post

        if (MinecraftForge.EVENT_BUS.post(startEvent)) {
            ITextComponent message = startEvent.getCancelReason() == null? new StringTextComponent("A mod has cancelled your login"): startEvent.getCancelReason();
            disconnect(message);
        }
    }

    @Inject(method = "tick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/login/ServerLoginNetHandler;handleAcceptedLogin()V"))
    public void onHello(CallbackInfo ci) {
        if (luckperms$waitFor == null) luckperms$waitFor = CompletableFuture.allOf(luckperms$waitConditions.toArray(new CompletableFuture[0]));
        if (!luckperms$waitFor.isDone()) ci.cancel();
    }
}
