package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

public interface ForgePlayer extends CommonsPlayer<ServerPlayerEntity, Entity> {
    ITextComponent getUserChatDisplayName();
    ITextComponent getUserTabListDisplayName();
    ITextComponent getUserCompactDisplayName();

    void setUserChatDisplayName(ITextComponent name);
    void setUserTabListDisplayName(ITextComponent name);
    void setUserCompactDisplayName(ITextComponent name);
}
