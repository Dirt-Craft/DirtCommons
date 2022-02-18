package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.ITextComponent;

public interface ForgePlayer extends CommonsPlayer<ServerPlayerEntity, Entity, ITextComponent>, ForgeCommandSource {
    ITextComponent getUserDisplayCarat();
    ITextComponent getUserDisplayPrefix();
    ITextComponent getUserDisplayIndicator();
    ITextComponent getUserDisplayName();
    ITextComponent getUserDisplaySuffix();

    @Override
    default void sendTitleTimes(int fadeIn, int stay, int fadeOut) {
        this.getServerEntity().connection.send(new STitlePacket(STitlePacket.Type.TIMES, null, fadeIn, stay, fadeOut));
    }

    @Override
    default void sendTitle(ITextComponent title) {
        this.getServerEntity().connection.send(new STitlePacket(STitlePacket.Type.TITLE, title));
    }

    @Override
    default void sendSubTitle(ITextComponent title) {
        this.getServerEntity().connection.send(new STitlePacket(STitlePacket.Type.SUBTITLE, title));
    }

    @Override
    default void clearTitle() {
        this.getServerEntity().connection.send(new STitlePacket(STitlePacket.Type.CLEAR, null, -1, -1, -1));
        this.getServerEntity().connection.send(new STitlePacket(STitlePacket.Type.RESET, null, -1, -1, -1));

    }

    interface ChatManager {
        void setUserDisplayCarat(ITextComponent carat);

        void setUserDisplayPrefix(ITextComponent prefix);

        void setUserDisplayIndicator(ITextComponent Indicator);

        void setUserDisplayName(ITextComponent displayName);

        void setUserDisplaySuffix(ITextComponent suffix);
    }
}
