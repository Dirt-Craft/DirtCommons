package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.TrackedViewerPacket;
import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SPlayerListItemPacket.class)
public class SPlayerListItemPacketMixin implements TrackedViewerPacket {
    @Unique private ServerPlayerEntity viewer;
    @Shadow @Final private List<SPlayerListItemPacket.AddPlayerData> entries;

    @Inject(method = "write", at = @At("HEAD"))
    public void onWrite(PacketBuffer buf, CallbackInfo ci) {
        if (viewer == null) return;
        this.entries.forEach(e->{
            PlayerEntity player = viewer.level.getPlayerByUUID(e.getProfile().getId());
            if (!(viewer instanceof CommonsPlayer)
                    || !(player instanceof CommonsPlayer)
                    || viewer == player
                    || ((CommonsPlayer)player).getVanishLevel() <= 0) return;
            e.gameMode = GameType.SPECTATOR;
        });

    }

    @Override
    public void addViewerData(ServerPlayerEntity entity) {
        this.viewer = entity;
    }
}
