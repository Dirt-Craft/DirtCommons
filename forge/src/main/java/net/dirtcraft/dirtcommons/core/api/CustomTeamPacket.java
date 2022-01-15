package net.dirtcraft.dirtcommons.core.api;

import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public interface CustomTeamPacket {

    static CustomTeamPacket getInstance(){
        return (CustomTeamPacket) new STeamsPacket();
    }

    default STeamsPacket setData(TextFormatting color, String... player){
        return setData(null, color, player);
    }

    default STeamsPacket setData(IFormattableTextComponent prefix, String... player){
        return setData(prefix, null, player);
    }

    default STeamsPacket setData(CommonsPlayer player){
        return setData(player.getPrefix(), player.getSuffix(), player.getColor(), player.fbi$getName());
    }

    default STeamsPacket setData(IFormattableTextComponent prefix, TextFormatting color, String... player){
        return setData(prefix, null, color, player);
    }

    STeamsPacket setData(IFormattableTextComponent prefix, IFormattableTextComponent suffix, TextFormatting color, String... player);

}
