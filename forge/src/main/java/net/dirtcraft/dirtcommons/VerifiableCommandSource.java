package net.dirtcraft.dirtcommons;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface VerifiableCommandSource {
    boolean isCommandCart();
    boolean isCommandBlock();
    boolean isConsole();
    boolean isPlayer();
    ServerPlayerEntity getPlayer();
}
