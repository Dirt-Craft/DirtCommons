package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

public interface ForgePlayer extends CommonsPlayer<ServerPlayerEntity, Entity, ITextComponent> {
    ITextComponent getUserDisplayCarat();
    ITextComponent getUserDisplayPrefix();
    ITextComponent getUserDisplayIndicator();
    ITextComponent getUserDisplayName();
    ITextComponent getUserDisplaySuffix();

    interface ChatManager {
        void setUserDisplayCarat(ITextComponent carat);

        void setUserDisplayPrefix(ITextComponent prefix);

        void setUserDisplayIndicator(ITextComponent Indicator);

        void setUserDisplayName(ITextComponent displayName);

        void setUserDisplaySuffix(ITextComponent suffix);
    }
}
