package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.dirtcraft.dirtcommons.util.ColorUtils;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
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

    default STeamsPacket setData(ForgePlayer player){
        return setData(player.getPrefix(), player.getSuffix(), ColorUtils.fromLegacy(player.getColor()), player.getUserName());
    }

    default STeamsPacket setData(ITextComponent prefix, TextFormatting color, String... player){
        return setData(prefix, null, color, player);
    }

    STeamsPacket setData(ITextComponent prefix, ITextComponent suffix, TextFormatting color, String... player);

}
