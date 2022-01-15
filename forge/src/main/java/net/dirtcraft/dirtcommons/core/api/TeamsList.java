package net.dirtcraft.dirtcommons.core.api;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface TeamsList {
    static TeamsList getInstance(){
        return (TeamsList) ServerLifecycleHooks.getCurrentServer().getPlayerList();
    }

    void addCustomData(ServerPlayerEntity player, TextFormatting color, IFormattableTextComponent prefix);

    void removeCustomData(ServerPlayerEntity player);

    void onPlayerLogoffEvent(PlayerEvent.PlayerLoggedOutEvent event);
}
