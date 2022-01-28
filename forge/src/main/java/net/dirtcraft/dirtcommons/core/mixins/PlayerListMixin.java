package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.CustomTeamPacket;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.storage.PlayerData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin implements net.dirtcraft.dirtcommons.user.PlayerList<ForgePlayer> {
    @Shadow public abstract List<ServerPlayerEntity> getPlayers();
    @Shadow @javax.annotation.Nullable public abstract ServerPlayerEntity getPlayer(UUID p_177451_1_);
    private final Set<ForgePlayer> customTeams = new HashSet<>();

    @Override
    public List<ForgePlayer> getOnlinePlayers() {
        //noinspection unchecked
        return (List<ForgePlayer>)(Object) getPlayers();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onConstruction(MinecraftServer p_i231425_1_, DynamicRegistries.Impl p_i231425_2_, PlayerData p_i231425_3_, int p_i231425_4_, CallbackInfo ci){
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLogoffEvent);
    }

    @Inject(method = "updateEntireScoreboard", at = @At("TAIL"))
    public void onUpdateScoreboard(ServerScoreboard p_96456_1_, ServerPlayerEntity p_96456_2_, CallbackInfo ci) {
        customTeams.forEach(p->p_96456_2_.connection.send(CustomTeamPacket.getInstance().setData(p)));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void onPlayerLogoffEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        customTeams.remove(event.getPlayer());
    }

    @Override
    public @Nullable ForgePlayer getOnlinePlayer(UUID uuid) {
        return (ForgePlayer) getPlayer(uuid);
    }

    @Override
    public void update(ForgePlayer player) {
        customTeams.forEach(p->player.getServerEntity().connection.send(CustomTeamPacket.getInstance().setData(p)));
    }

    @Override
    public void addPseudoTeam(ForgePlayer player) {
        customTeams.add(player);
        update(player);
    }

    @Override
    public void removePseudoTeam(ForgePlayer player) {
        customTeams.remove(player);
        update(player);
    }
}
