package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

public interface ForgePlayer extends CommonsPlayer<ServerPlayerEntity, Entity> {
    ITextComponent getPrefix();
    void setPrefix(ITextComponent prefix);

    ITextComponent getSuffix();
    void setSuffix(ITextComponent suffix);

    ITextComponent getDisplayName();
    void setDisplayName(ITextComponent name);
}
