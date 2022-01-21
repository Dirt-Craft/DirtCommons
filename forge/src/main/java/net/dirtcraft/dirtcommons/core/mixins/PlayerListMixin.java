package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.CustomTeamPacket;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.core.api.TeamsList;
import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.dirtcraft.dirtcommons.util.ColorUtils;
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
public abstract class PlayerListMixin implements TeamsList {

    @Shadow public abstract void broadcastAll(IPacket<?> p_148540_1_);

    private final List<ForgePlayer> customTeams = new ArrayList<>();
    @Inject(method = "updateEntireScoreboard", at = @At("TAIL"))
    public void onUpdateScoreboard(ServerScoreboard p_96456_1_, ServerPlayerEntity p_96456_2_, CallbackInfo ci) {
        customTeams.forEach(p->p_96456_2_.connection.send(CustomTeamPacket.getInstance().setData(p)));
    }

    @Override
    public void addCustomData(ServerPlayerEntity player, TextFormatting color, IFormattableTextComponent prefix) {
        ForgePlayer playerSettings = (ForgePlayer) player;
        if (color != null) playerSettings.setColor(ColorUtils.toLegacy(color));
        if (prefix != null) playerSettings.setPrefix(prefix.append(" "));
        if (!customTeams.contains(player)) customTeams.add(playerSettings);
        broadcastAll(CustomTeamPacket.getInstance().setData(playerSettings));
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public void removeCustomData(ServerPlayerEntity player) {
        customTeams.remove(player);
        broadcastAll(CustomTeamPacket.getInstance().setData((ForgePlayer) player));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void onPlayerLogoffEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        customTeams.remove(event.getPlayer());
    }
}
