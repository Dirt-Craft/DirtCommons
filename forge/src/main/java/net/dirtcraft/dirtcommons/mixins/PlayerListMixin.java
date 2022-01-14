package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.TeamsPacket;
import net.dirtcraft.vanish.AgentList;
import net.dirtcraft.vanish.FBIAgent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin implements AgentList {
    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow public abstract void broadcastAll(IPacket<?> p_148540_1_);

    private final List<FBIAgent> customTeams = new ArrayList<>();
    @Inject(method = "updateEntireScoreboard", at = @At("TAIL"))
    public void onUpdateScoreboard(ServerScoreboard p_96456_1_, ServerPlayerEntity p_96456_2_, CallbackInfo ci) {
        customTeams.forEach(p->p_96456_2_.connection.send(TeamsPacket.getInstance().setData(p)));
    }

    @Override
    public void addCustomData(ServerPlayerEntity player, TextFormatting color, IFormattableTextComponent prefix) {
        FBIAgent playerSettings = (FBIAgent) player;
        playerSettings.setColor(color);
        playerSettings.setPrefix(prefix.append(" "));
        if (!customTeams.contains(player)) customTeams.add(playerSettings);
        broadcastAll(TeamsPacket.getInstance().setData(playerSettings));
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public void removeCustomData(ServerPlayerEntity player) {
        customTeams.remove(player);
        broadcastAll(TeamsPacket.getInstance().setData((FBIAgent) player));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void onPlayerLogoffEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        customTeams.remove(event.getPlayer());
    }
}
