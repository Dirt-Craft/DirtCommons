package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;
import java.util.List;

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

    default void sendBlockUpdate(BlockPos pos, BlockState block){
        getServerEntity().connection.send(new SChangeBlockPacket(pos, block));
    }

    default void refreshBlock(BlockPos pos){
        getServerEntity().connection.send(new SChangeBlockPacket(pos, getServerEntity().level.getBlockState(pos)));
    }

    default void sendBlockUpdates(Collection<BlockPos> locs, BlockState block){
        locs.forEach(pos-> getServerEntity().connection.send(new SChangeBlockPacket(pos, block)));
    }

    default void refreshBlocks(Collection<BlockPos> locs){
        World w = getServerEntity().level;
        locs.forEach(pos-> getServerEntity().connection.send(new SChangeBlockPacket(pos, w.getBlockState(pos))));
    }

    interface ChatManager {
        void setUserDisplayCarat(ITextComponent carat);

        void setUserDisplayPrefix(ITextComponent prefix);

        void setUserDisplayIndicator(ITextComponent Indicator);

        void setUserDisplayName(ITextComponent displayName);

        void setUserDisplaySuffix(ITextComponent suffix);
    }
}
